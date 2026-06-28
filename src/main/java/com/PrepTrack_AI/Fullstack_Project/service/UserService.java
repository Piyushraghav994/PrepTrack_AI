package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateUserStatusRequest;

import java.util.List;

/**
 * Service interface for User profile management and administrative operations.
 */
public interface UserService {

    /**
     * Retrieve profile of the user identified by their email.
     */
    ApiResponse<UserProfileDTO> getUserProfile(String email);

    /**
     * Update profile of the user identified by their email.
     */
    ApiResponse<UserProfileDTO> updateUserProfile(String email, UserRequestDTO request);

    /**
     * Retrieve profiles of all users (Admin operation).
     */
    ApiResponse<List<UserResponseDTO>> getAllUsers();

    /**
     * Update user account status or roles (Admin operation).
     */
    ApiResponse<UserProfileDTO> updateUserStatus(Long userId, UpdateUserStatusRequest request);
}
