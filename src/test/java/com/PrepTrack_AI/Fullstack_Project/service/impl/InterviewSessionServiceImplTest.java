package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.entity.*;
import com.PrepTrack_AI.Fullstack_Project.mapper.InterviewSessionMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewFeedbackRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewSessionRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.UserProgressService;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewSessionServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private InterviewSessionRepository interviewSessionRepository;

    @Mock
    private InterviewFeedbackRepository interviewFeedbackRepository;

    @Mock
    private InterviewSessionMapper interviewSessionMapper;

    @Mock
    private UserProgressService userProgressService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InterviewSessionServiceImpl interviewSessionService;

    private User user;
    private Interview interview;
    private InterviewSession session;
    private InterviewSessionResponseDTO sessionResponseDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("student@example.com")
                .fullName("Student Name")
                .build();

        interview = Interview.builder()
                .id(2L)
                .title("Java Interview")
                .company("Google")
                .role("Software Engineer")
                .difficulty(Difficulty.MEDIUM)
                .questions(Collections.emptyList())
                .build();

        session = InterviewSession.builder()
                .id(3L)
                .user(user)
                .interview(interview)
                .startTime(LocalDateTime.now())
                .build();

        sessionResponseDTO = InterviewSessionResponseDTO.builder()
                .id(3L)
                .userId(1L)
                .interviewId(2L)
                .interviewTitle("Java Interview")
                .startTime(session.getStartTime())
                .build();
    }

    @Test
    void startSession_Success() {
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(interviewRepository.findById(2L)).thenReturn(Optional.of(interview));
        when(interviewSessionRepository.save(any(InterviewSession.class))).thenReturn(session);
        when(interviewSessionMapper.toResponseDTO(session)).thenReturn(sessionResponseDTO);

        ApiResponse<InterviewSessionResponseDTO> response = interviewSessionService.startSession("student@example.com", 2L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(3L, response.getData().getId());
    }

    @Test
    void submitSession_Success() {
        SubmitSessionRequestDTO submitReq = SubmitSessionRequestDTO.builder()
                .score(85)
                .build();

        when(interviewSessionRepository.findById(3L)).thenReturn(Optional.of(session));
        when(interviewSessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interviewFeedbackRepository.save(any(InterviewFeedback.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(userProgressService).updateProgressAfterSession(any(User.class), anyInt(), anyInt());
        
        sessionResponseDTO.setScore(85);
        when(interviewSessionMapper.toResponseDTO(any(InterviewSession.class))).thenReturn(sessionResponseDTO);

        ApiResponse<InterviewSessionResponseDTO> response = interviewSessionService.submitSession(3L, submitReq);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(85, response.getData().getScore());
        verify(userProgressService, times(1)).updateProgressAfterSession(user, 85, 5);
    }
}
