package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateProfileRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateUserStatusRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileResponse;

import java.util.List;

/**
 * Service interface for User profile management and administrative operations.
 */
public interface UserService {

    /**
     * Retrieve profile of the user identified by their email.
     */
    ApiResponse<UserProfileResponse> getUserProfile(String email);

    /**
     * Update profile of the user identified by their email.
     */
    ApiResponse<UserProfileResponse> updateUserProfile(String email, UpdateProfileRequest request);

    /**
     * Retrieve profiles of all users (Admin operation).
     */
    ApiResponse<List<UserProfileResponse>> getAllUsers();

    /**
     * Update user account status or roles (Admin operation).
     */
    ApiResponse<UserProfileResponse> updateUserStatus(Long userId, UpdateUserStatusRequest request);
}
