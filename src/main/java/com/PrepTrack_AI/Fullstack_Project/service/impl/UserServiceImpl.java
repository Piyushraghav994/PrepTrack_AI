package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateProfileRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateUserStatusRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileResponse;
import com.PrepTrack_AI.Fullstack_Project.entity.Role;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.exception.UserNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.repository.RoleRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link UserService} interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UserProfileResponse> getUserProfile(String email) {
        log.info("Fetching profile for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return ApiResponse.success("Profile fetched successfully", mapToProfileResponse(user));
    }

    @Override
    public ApiResponse<UserProfileResponse> updateUserProfile(String email, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Update profile fields
        user.setFullName(request.getFullName());
        user.setCollege(request.getCollege());
        user.setBranch(request.getBranch());
        user.setPassoutYear(request.getPassoutYear());
        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setLinkedinUrl(request.getLinkedinUrl());
        user.setGithubUrl(request.getGithubUrl());
        user.setResumeUrl(request.getResumeUrl());

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", email);
        return ApiResponse.success("Profile updated successfully", mapToProfileResponse(updatedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<UserProfileResponse>> getAllUsers() {
        log.info("Fetching profiles of all users");
        List<User> users = userRepository.findAll();
        List<UserProfileResponse> responses = users.stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("All users fetched successfully", responses);
    }

    @Override
    public ApiResponse<UserProfileResponse> updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        log.info("Updating status for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

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
        log.info("User status updated successfully for user ID: {}", userId);
        return ApiResponse.success("User status updated successfully", mapToProfileResponse(updatedUser));
    }

    /**
     * Map User entity to UserProfileResponse DTO.
     */
    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .college(user.getCollege())
                .branch(user.getBranch())
                .passoutYear(user.getPassoutYear())
                .profileImageUrl(user.getProfileImageUrl())
                .phoneNumber(user.getPhoneNumber())
                .linkedinUrl(user.getLinkedinUrl())
                .githubUrl(user.getGithubUrl())
                .resumeUrl(user.getResumeUrl())
                .emailVerified(user.getEmailVerified())
                .accountNonLocked(user.getAccountNonLocked())
                .accountNonExpired(user.getAccountNonExpired())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .enabled(user.getEnabled())
                .lastLoginAt(user.getLastLoginAt())
                .lastLoginIp(user.getLastLoginIp())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
}
