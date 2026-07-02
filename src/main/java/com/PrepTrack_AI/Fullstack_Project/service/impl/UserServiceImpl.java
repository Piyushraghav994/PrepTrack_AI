package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateUserStatusRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.PrepTrack_AI.Fullstack_Project.storage.StorageService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationService;
import com.PrepTrack_AI.Fullstack_Project.entity.NotificationType;
import com.PrepTrack_AI.Fullstack_Project.entity.Role;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.exception.UserNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.mapper.UserMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.RoleRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the {@link UserService} interface.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final StorageService storageService;
    private final NotificationService notificationService;

    @Override
    public ApiResponse<UserProfileDTO> updateProfilePicture(String email, MultipartFile file) throws IOException {
        logger.info("Uploading profile picture for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Delete existing profile image from storage if it exists and is not simulation
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().contains("mock-cloud") && !user.getProfileImageUrl().contains("dummy")) {
            try {
                storageService.deleteFile(user.getProfileImageUrl());
            } catch (Exception e) {
                logger.warn("Failed to delete existing profile image: {}", user.getProfileImageUrl(), e);
            }
        }

        // Upload new file
        String newUrl = storageService.uploadFile(file, "profiles");
        user.setProfileImageUrl(newUrl);

        User updatedUser = userRepository.save(user);
        logger.info("Profile picture updated successfully for user: {} with URL: {}", email, newUrl);
        
        notificationService.sendNotification(updatedUser, "Profile Picture Updated", "Your profile picture has been uploaded and updated successfully.", NotificationType.SUCCESS);

        return ApiResponse.success("Profile picture updated successfully", userMapper.toProfileDTO(updatedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UserProfileDTO> getUserProfile(String email) {
        logger.debug("Fetching user details for: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Database operation failed: User not found with email {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
        return ApiResponse.success("Profile fetched successfully", userMapper.toProfileDTO(user));
    }

    @Override
    public ApiResponse<UserProfileDTO> updateUserProfile(String email, UserRequestDTO request) {
        logger.info("Updating profile for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Database operation failed: User not found with email {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });

        // Use MapStruct to map updates onto the existing entity
        userMapper.updateUserFromDto(request, user);

        User updatedUser = userRepository.save(user);
        logger.info("Profile updated successfully for user: {}", email);
        
        notificationService.sendNotification(updatedUser, "Profile Updated", "Your profile details have been successfully updated.", NotificationType.INFO);

        return ApiResponse.success("Profile updated successfully", userMapper.toProfileDTO(updatedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<UserResponseDTO>> getAllUsers(int page, int size) {
        logger.info("Fetching profiles of all users, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponseDTO> content = userMapper.toResponseDTOList(userPage.getContent());

        PagedResponse<UserResponseDTO> response = PagedResponse.<UserResponseDTO>builder()
                .content(content)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();

        return ApiResponse.success("All users fetched successfully", response);
    }

    @Override
    public ApiResponse<UserProfileDTO> updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        logger.info("Updating status for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Database operation failed: User not found with ID {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        if (request.getRoleName() != null) {
            Role role = roleRepository.findByName(request.getRoleName())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", request.getRoleName()));
            user.setRole(role);
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        if (request.getAccountNonLocked() != null) {
            user.setAccountNonLocked(request.getAccountNonLocked());
        }

        if (request.getAccountNonExpired() != null) {
            user.setAccountNonExpired(request.getAccountNonExpired());
        }

        if (request.getCredentialsNonExpired() != null) {
            user.setCredentialsNonExpired(request.getCredentialsNonExpired());
        }

        User updatedUser = userRepository.save(user);
        logger.info("User status updated successfully for user ID: {}", userId);
        return ApiResponse.success("User status updated successfully", userMapper.toProfileDTO(updatedUser));
    }
}
