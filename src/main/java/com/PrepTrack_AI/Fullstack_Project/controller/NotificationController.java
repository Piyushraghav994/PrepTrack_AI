package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.NotificationResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for managing user notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification System", description = "Endpoints for viewing and managing user notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's Notifications", description = "Retrieves a paginated list of notifications for the authenticated user.")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponseDTO>>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        log.debug("Get notifications request: user={}, page={}, size={}", principal.getName(), page, size);
        return ResponseEntity.ok(notificationService.getUserNotifications(principal.getName(), page, size));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark Notification as Read", description = "Marks a specific notification as read.")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> markAsRead(
            @PathVariable Long id,
            Principal principal) {
        log.info("Mark notification read request: id={}, user={}", id, principal.getName());
        return ResponseEntity.ok(notificationService.markAsRead(principal.getName(), id));
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark All Notifications as Read", description = "Marks all unread notifications as read for the authenticated user.")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Principal principal) {
        log.info("Mark all notifications read request: user={}", principal.getName());
        return ResponseEntity.ok(notificationService.markAllAsRead(principal.getName()));
    }
}
