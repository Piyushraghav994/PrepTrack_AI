package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateUserStatusRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;
import com.PrepTrack_AI.Fullstack_Project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for User profile management and administration.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for Profile Management and Administrator User Operations")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get user profile",
            description = "Retrieves the profile information of the currently authenticated user."
    )
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    @PostMapping(value = "/profile/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Upload and update profile picture",
            description = "Uploads a raw profile image, stores it using the active cloud provider, and updates the user's profile image URL."
    )
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfilePicture(
            @RequestParam("file") MultipartFile file,
            Principal principal) throws IOException {
        return ResponseEntity.ok(userService.updateProfilePicture(principal.getName(), file));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update user profile",
            description = "Updates the profile information of the currently authenticated user."
    )
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            @Valid @RequestBody UserRequestDTO request,
            Principal principal) {
        return ResponseEntity.ok(userService.updateUserProfile(principal.getName(), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all platform user profiles. Admin only."
    )
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Update user status",
            description = "Updates a user's role or account security status flags (enabled, lock status, expiration). Admin only."
    )
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(userService.updateUserStatus(id, request));
    }
}
