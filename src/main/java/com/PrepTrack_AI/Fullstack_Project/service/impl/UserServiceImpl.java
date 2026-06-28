package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UpdateUserStatusRequest;
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
        return ApiResponse.success("Profile updated successfully", userMapper.toProfileDTO(updatedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        logger.info("Fetching profiles of all users");
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> responses = userMapper.toResponseDTOList(users);
        return ApiResponse.success("All users fetched successfully", responses);
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
