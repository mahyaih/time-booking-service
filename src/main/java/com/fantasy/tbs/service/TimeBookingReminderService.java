package com.fantasy.tbs.service;

public interface TimeBookingReminderService {
    /**
     * A scheduled job running every week day at end of day that
     * find all employees with missing time bookings for current day
     * and sends booking correction notification (email, sms) to them
     */
    void sendTimeBookingNotifications();
}
