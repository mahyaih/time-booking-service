package com.fantasy.tbs.service;

import com.fantasy.tbs.domain.TimeBookDTO;
import com.fantasy.tbs.domain.TimeBooking;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service Interface for managing {@link TimeBooking}.
 */
public interface TimeBookingService {
    /**
     * Save a timeBooking.
     *
     * @param timeBooking the entity to save.
     * @return the persisted entity.
     */
    TimeBooking save(TimeBooking timeBooking);

    /**
     * Partially updates a timeBooking.
     *
     * @param timeBooking the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TimeBooking> partialUpdate(TimeBooking timeBooking);

    /**
     * Get all the timeBookings.
     *
     * @return the list of entities.
     */
    List<TimeBooking> findAll();

    /**
     * Get the "id" timeBooking.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TimeBooking> findOne(Long id);

    /**
     * Delete the "id" timeBooking.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Saves the new booking time to the database
     *
     * @param timeBookDTO
     */
    void bookTime(TimeBookDTO timeBookDTO);

    /**
     * Get the working hours of an employee
     *
     * @param personalNumber of an employee
     * @return the working hours of an employee in minutes
     */
    Long getWorkingHours(String personalNumber);

    /**
     * Finds set of personal numbers of employees
     * with correct time bookings (2) for current day
     * @return set of personal numbers
     */
    Set<String> findEmployeesWithCorrectBooking();
}
