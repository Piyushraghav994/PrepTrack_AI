package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.entity.Notification;

/**
 * Interface representing a channel for sending notifications (e.g., In-App, Email, Push).
 */
public interface NotificationSender {

    /**
     * Sends a notification through the channel.
     *
     * @param notification The notification entity to send
     */
    void send(Notification notification);
}
