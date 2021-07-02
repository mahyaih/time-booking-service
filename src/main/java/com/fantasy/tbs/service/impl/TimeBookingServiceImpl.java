package com.fantasy.tbs.service.impl;

import com.fantasy.tbs.domain.TimeBookDTO;
import com.fantasy.tbs.domain.TimeBooking;
import com.fantasy.tbs.repository.TimeBookingRepository;
import com.fantasy.tbs.service.TimeBookingService;
import com.fantasy.tbs.service.mapper.TimeBookMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * Service Implementation for managing {@link TimeBooking}.
 */
@Service
@Transactional
public class TimeBookingServiceImpl implements TimeBookingService {

    private final Logger log = LoggerFactory.getLogger(TimeBookingServiceImpl.class);

    private final TimeBookingRepository timeBookingRepository;
    private final TimeBookMapper timeBookMapper;

    public TimeBookingServiceImpl(TimeBookingRepository timeBookingRepository, TimeBookMapper timeBookMapper) {
        this.timeBookingRepository = timeBookingRepository;
        this.timeBookMapper = timeBookMapper;
    }

    @Override
    public TimeBooking save(TimeBooking timeBooking) {
        log.debug("Request to save TimeBooking : {}", timeBooking);
        return timeBookingRepository.save(timeBooking);
    }

    @Override
    public Optional<TimeBooking> partialUpdate(TimeBooking timeBooking) {
        log.debug("Request to partially update TimeBooking : {}", timeBooking);

        return timeBookingRepository
            .findById(timeBooking.getId())
            .map(
                existingTimeBooking -> {
                    if (timeBooking.getBooking() != null) {
                        existingTimeBooking.setBooking(timeBooking.getBooking());
                    }
                    if (timeBooking.getPersonalNumber() != null) {
                        existingTimeBooking.setPersonalNumber(timeBooking.getPersonalNumber());
                    }

                    return existingTimeBooking;
                }
            )
            .map(timeBookingRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeBooking> findAll() {
        log.debug("Request to get all TimeBookings");
        return timeBookingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TimeBooking> findOne(Long id) {
        log.debug("Request to get TimeBooking : {}", id);
        return timeBookingRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TimeBooking : {}", id);
        timeBookingRepository.deleteById(id);
    }

    @Override
    public void bookTime(TimeBookDTO timeBookDTO) {
        timeBookingRepository.save(timeBookMapper.toTimeBooking(timeBookDTO));
    }

    @Override
    public Long getWorkingHours(String personalNumber) {
        log.debug("Request to get working hours for personal number: {}", personalNumber);
        // improvement: working time should be filtered by date range
        var timeBookings = timeBookingRepository.findByPersonalNumber(personalNumber);
        var timeBookingsPerDay = timeBookings.stream().collect(groupingBy(TimeBooking::getLocalDate));
        var workingMinutes = 0L;
        for (final LocalDate localDate : timeBookingsPerDay.keySet()) {
            workingMinutes += calculateWorkMinutes(localDate, timeBookingsPerDay.get(localDate));
        }
        return workingMinutes / 60;
    }

    @Override
    public Set<String> findEmployeesWithCorrectBooking() {
        var timeBookings = timeBookingRepository
            .findByBookingAfter(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()));

        var timeBookingsPerEmployee = timeBookings.stream()
            .collect(groupingBy(TimeBooking::getPersonalNumber));

        Set<String> employeesWithTwoBookings = new HashSet<>();
        for (Map.Entry<String, List<TimeBooking>> entry : timeBookingsPerEmployee.entrySet()) {
            // assumption: 2 bookings (check-in and check-out) are number of correct bookings
            if (entry.getValue().size() == 2) {
                employeesWithTwoBookings.add(entry.getKey());
            } else {
                log.warn("Employee with personal number: {} has incorrect number of bookings: {}",
                    entry.getKey(), entry.getValue().size());
            }
        }

        log.info("Found {} of employees with correct booking times.", employeesWithTwoBookings);
        return employeesWithTwoBookings;
    }

    /**
     * Assumption: 2 bookings (check-in and check-out) are number of correct bookings
     * this can be improved to calculate time when size is more than 2
     * or it can add a default work time for that day if one time booking exist
     */
    private Long calculateWorkMinutes(LocalDate localDate, List<TimeBooking> timeBookings) {
        if (timeBookings.size() != 2) {
            log.warn("Can not calculate working time for day: {} because of wrong number of bookings: {}",
                localDate, timeBookings.size());
        }

        return Math.abs(ChronoUnit.MINUTES.between(timeBookings.get(0).getBooking(), timeBookings.get(1).getBooking()));
    }
}
