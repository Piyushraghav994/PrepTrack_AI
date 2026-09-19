package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateUserStatusRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

/**
 * Service interface for User profile management and administrative operations.
 */
public interface UserService {

    /**
     * Upload profile picture and update user profile (Cloud Storage integration).
     */
    ApiResponse<UserProfileDTO> updateProfilePicture(String email, MultipartFile file) throws IOException;

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
    ApiResponse<PagedResponse<UserResponseDTO>> getAllUsers(int page, int size);

    /**
     * Update user account status or roles (Admin operation).
     */
    ApiResponse<UserProfileDTO> updateUserStatus(Long userId, UpdateUserStatusRequest request);
}
