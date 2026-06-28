package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProgressResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.entity.UserProgress;
import com.PrepTrack_AI.Fullstack_Project.mapper.UserProgressMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.UserProgressRepository;
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
class UserProgressServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    @Mock
    private UserProgressMapper userProgressMapper;

    @InjectMocks
    private UserProgressServiceImpl userProgressService;

    private User user;
    private UserProgress progress;
    private UserProgressResponseDTO progressResponseDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("student@example.com")
                .fullName("Student Name")
                .build();

        progress = UserProgress.builder()
                .id(2L)
                .user(user)
                .completedInterviews(1)
                .completedQuestions(3)
                .currentStreak(1)
                .totalScore(85)
                .build();

        progressResponseDTO = UserProgressResponseDTO.builder()
                .id(2L)
                .userId(1L)
                .userEmail("student@example.com")
                .completedInterviews(1)
                .completedQuestions(3)
                .currentStreak(1)
                .totalScore(85)
                .build();
    }

    @Test
    void getUserProgress_Success() {
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(userProgressRepository.findByUserId(1L)).thenReturn(Optional.of(progress));
        when(userProgressMapper.toResponseDTO(progress)).thenReturn(progressResponseDTO);

        ApiResponse<UserProgressResponseDTO> response = userProgressService.getUserProgress("student@example.com");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(85, response.getData().getTotalScore());
        assertEquals(1, response.getData().getCurrentStreak());
    }

    @Test
    void updateProgressAfterSession_Success() {
        when(userProgressRepository.findByUserId(1L)).thenReturn(Optional.of(progress));
        when(userProgressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));

        userProgressService.updateProgressAfterSession(user, 90, 5);

        assertEquals(2, progress.getCompletedInterviews());
        assertEquals(8, progress.getCompletedQuestions());
        assertEquals(175, progress.getTotalScore());
        assertEquals(2, progress.getCurrentStreak());
        verify(userProgressRepository, times(1)).save(progress);
    }
}
