package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.entity.Notification;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mock push notification channel for future mobile notification gateway integration.
 */
@Service("pushNotificationSender")
@Slf4j
public class PushNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("[FUTURE-PUSH-NOTIFICATION] Sending push alert to user ID: {} | Title: {} | Body: {}",
                notification.getUser().getId(),
                notification.getTitle(),
                notification.getMessage());
    }
}
