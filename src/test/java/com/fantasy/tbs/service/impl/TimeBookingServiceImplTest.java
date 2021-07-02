package com.fantasy.tbs.service.impl;

import com.fantasy.tbs.domain.TimeBooking;
import com.fantasy.tbs.repository.TimeBookingRepository;
import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TimeBookingServiceImplTest {

    private static final String PERSONAL_NUMBER_1 = "personal-number-01";
    private static final String PERSONAL_NUMBER_2 = "personal-number-02";
    private static final String PERSONAL_NUMBER_3 = "personal-number-03";

    @InjectMocks
    private TimeBookingServiceImpl timeBookingServiceImpl;

    @Mock
    private TimeBookingRepository timeBookingRepository;

    @Test
    public void getWorkingHours_should_return_correct_working_hours() {
        Mockito.when(timeBookingRepository.findByPersonalNumber(PERSONAL_NUMBER_1)).thenReturn(createBookings());

        Long result = timeBookingServiceImpl.getWorkingHours(PERSONAL_NUMBER_1);
        Assertions.assertThat(result).isEqualTo(17);
    }

    @Test
    public void findEmployeesWithCorrectBooking_should_return_employees_with_two_bookings() {
        var allBookingsToday = Arrays.asList(
            createTimeBooking(PERSONAL_NUMBER_1, 0, 8),
            createTimeBooking(PERSONAL_NUMBER_1, 0, 16),
            createTimeBooking(PERSONAL_NUMBER_2, 0, 18),
            createTimeBooking(PERSONAL_NUMBER_3, 0, 8),
            createTimeBooking(PERSONAL_NUMBER_3, 0, 9),
            createTimeBooking(PERSONAL_NUMBER_3, 0, 11)
        );
        Mockito.when(timeBookingRepository.findByBookingAfter(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault())))
            .thenReturn(allBookingsToday);

        var result = timeBookingServiceImpl.findEmployeesWithCorrectBooking();
        Assertions.assertThat(result).isEqualTo(Sets.newHashSet(PERSONAL_NUMBER_1));
    }

    private List<TimeBooking> createBookings() {
        return Arrays.asList(
            createTimeBooking(PERSONAL_NUMBER_1, 0, 8),
            createTimeBooking(PERSONAL_NUMBER_1, 0, 16),
            createTimeBooking(PERSONAL_NUMBER_1, 1, 8),
            createTimeBooking(PERSONAL_NUMBER_1, 1, 17)
        );
    }

    private TimeBooking createTimeBooking(String personalNumber, Integer daysBefore, Integer hourOfDay) {
        TimeBooking timeBooking = new TimeBooking();
        timeBooking.setPersonalNumber(personalNumber);
        timeBooking.setBooking(ZonedDateTime.of(
            LocalDate.now().minusDays(daysBefore),
            LocalTime.of(hourOfDay, 0),
            ZoneId.systemDefault()));
        return timeBooking;
    }
}
