package com.fantasy.tbs.service.impl;

import com.fantasy.tbs.service.EmployeeService;
import com.fantasy.tbs.service.NotificationService;
import com.fantasy.tbs.service.TimeBookingReminderService;
import com.fantasy.tbs.service.TimeBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TimeBookingReminderServiceImpl implements TimeBookingReminderService {

    private final Logger log = LoggerFactory.getLogger(TimeBookingReminderServiceImpl.class);

    private final TimeBookingService timeBookingService;
    private final EmployeeService employeeService;
    private final NotificationService notificationService;

    public TimeBookingReminderServiceImpl(TimeBookingService timeBookingService, EmployeeService employeeService, NotificationService notificationService) {
        this.timeBookingService = timeBookingService;
        this.employeeService = employeeService;
        this.notificationService = notificationService;
    }

    /**
     * The cron job expression needs to be further verified and
     * can be improved to be configurable
     * The current job runs every weekday at 18:00
     */
    @Override
    @Scheduled(cron = "0 18 * * 1-5")
    public void sendTimeBookingNotifications() {
        log.info("Started to send booking notifications.");
        var employeesWithCorrectBooking = timeBookingService.findEmployeesWithCorrectBooking();
        Set<String> allEmployeesWorkingToday = employeeService.findAllEmployeesWorkingToday();

        for (String personalNumber : allEmployeesWorkingToday) {
            if (!employeesWithCorrectBooking.contains(personalNumber)) {
                notificationService.sendBookingReminder(personalNumber);
            }
        }
        log.info("Finished sending booking notifications.");
    }
}
