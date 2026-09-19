package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.NotificationResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;
import com.PrepTrack_AI.Fullstack_Project.entity.Notification;
import com.PrepTrack_AI.Fullstack_Project.entity.NotificationType;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.exception.UserNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.repository.NotificationRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationSender;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link NotificationService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> notificationSenders;

    @Override
    public void sendNotification(User user, String title, String message, NotificationType type) {
        log.info("Creating notification for user: {}. Title: {}", user.getEmail(), title);
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .user(user)
                .build();

        Notification saved = notificationRepository.save(notification);

        // Deliver notification via all configured channels
        for (NotificationSender sender : notificationSenders) {
            try {
                sender.send(saved);
            } catch (Exception e) {
                log.error("Failed to send notification via channel: {}", sender.getClass().getSimpleName(), e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<NotificationResponseDTO>> getUserNotifications(String email, int page, int size) {
        log.debug("Fetching notifications for user: {}, page: {}, size: {}", email, page, size);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        List<NotificationResponseDTO> content = notificationPage.getContent().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        PagedResponse<NotificationResponseDTO> response = PagedResponse.<NotificationResponseDTO>builder()
                .content(content)
                .pageNumber(notificationPage.getNumber())
                .pageSize(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .last(notificationPage.isLast())
                .build();

        return ApiResponse.success("Notifications fetched successfully", response);
    }

    @Override
    public ApiResponse<NotificationResponseDTO> markAsRead(String email, Long notificationId) {
        log.info("Marking notification ID {} as read for user: {}", notificationId, email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Notification does not belong to the authenticated user");
        }

        notification.setIsRead(true);
        Notification updated = notificationRepository.save(notification);
        return ApiResponse.success("Notification marked as read successfully", mapToResponseDTO(updated));
    }

    @Override
    public ApiResponse<Void> markAllAsRead(String email) {
        log.info("Marking all notifications as read for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        notificationRepository.markAllAsReadForUser(user.getId());
        return ApiResponse.success("All notifications marked as read successfully");
    }

    private NotificationResponseDTO mapToResponseDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
