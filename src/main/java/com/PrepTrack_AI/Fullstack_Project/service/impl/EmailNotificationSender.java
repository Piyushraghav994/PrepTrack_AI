package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.entity.Notification;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mock email notification channel for future SMTP integration.
 */
@Service("emailNotificationSender")
@Slf4j
public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("[FUTURE-EMAIL-NOTIFICATION] Sending email to: {} | Subject: {} | Message: {}",
                notification.getUser().getEmail(),
                notification.getTitle(),
                notification.getMessage());
    }
}
