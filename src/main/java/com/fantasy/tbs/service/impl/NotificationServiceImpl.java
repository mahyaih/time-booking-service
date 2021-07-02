package com.fantasy.tbs.service.impl;

import com.fantasy.tbs.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This service sends notifications (email, sms) to the contact of an employee
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void sendBookingReminder(String personalNumber) {
        // TODO: Implementation is needed
        log.info("Sending notification for time booking correction to employee with personal number: {}",
            personalNumber);
    }
}
