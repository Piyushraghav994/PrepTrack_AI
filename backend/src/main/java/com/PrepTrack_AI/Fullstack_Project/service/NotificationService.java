package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.NotificationResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;
import com.PrepTrack_AI.Fullstack_Project.entity.NotificationType;
import com.PrepTrack_AI.Fullstack_Project.entity.User;

/**
 * Service interface for managing user Notifications.
 */
public interface NotificationService {

    /**
     * Internal method to trigger and send notifications across all channels.
     */
    void sendNotification(User user, String title, String message, NotificationType type);

    /**
     * Fetches a paginated list of user notifications.
     */
    ApiResponse<PagedResponse<NotificationResponseDTO>> getUserNotifications(String email, int page, int size);

    /**
     * Marks a specific notification as read.
     */
    ApiResponse<NotificationResponseDTO> markAsRead(String email, Long notificationId);

    /**
     * Optimally marks all user notifications as read.
     */
    ApiResponse<Void> markAllAsRead(String email);
}
