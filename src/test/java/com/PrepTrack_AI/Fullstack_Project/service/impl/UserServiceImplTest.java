package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.Role;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.exception.UserNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.mapper.UserMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.RoleRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .name("ROLE_STUDENT")
                .build();

        user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("password")
                .college("Test College")
                .branch("Computer Science")
                .passoutYear(2025)
                .role(role)
                .emailVerified(true)
                .enabled(true)
                .build();
    }

    @Test
    void getUserProfile_Success() {
        UserProfileDTO profileDTO = UserProfileDTO.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .role("ROLE_STUDENT")
                .build();

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toProfileDTO(user)).thenReturn(profileDTO);

        ApiResponse<UserProfileDTO> response = userService.getUserProfile("john.doe@example.com");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("John Doe", response.getData().getFullName());
        assertEquals("john.doe@example.com", response.getData().getEmail());
        assertEquals("ROLE_STUDENT", response.getData().getRole());
    }

    @Test
    void getUserProfile_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserProfile("unknown@example.com");
        });
    }

    @Test
    void updateUserProfile_Success() {
        UserProfileDTO updatedProfileDTO = UserProfileDTO.builder()
                .fullName("John Updated")
                .college("New College")
                .branch("Mechanical")
                .passoutYear(2026)
                .build();

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toProfileDTO(any(User.class))).thenReturn(updatedProfileDTO);

        UserRequestDTO updateRequest = UserRequestDTO.builder()
                .fullName("John Updated")
                .college("New College")
                .branch("Mechanical")
                .passoutYear(2026)
                .build();

        ApiResponse<UserProfileDTO> response = userService.updateUserProfile("john.doe@example.com", updateRequest);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("John Updated", response.getData().getFullName());
        assertEquals("New College", response.getData().getCollege());
        assertEquals("Mechanical", response.getData().getBranch());
        assertEquals(2026, response.getData().getPassoutYear());
    }
}
