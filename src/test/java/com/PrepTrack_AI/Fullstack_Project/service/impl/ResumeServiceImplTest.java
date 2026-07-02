package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.Resume;
import com.PrepTrack_AI.Fullstack_Project.entity.ResumeAnalysis;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.mapper.ResumeMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.ResumeAnalysisRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.ResumeRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.UserProgressService;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationService;
import com.PrepTrack_AI.Fullstack_Project.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @Mock
    private ResumeMapper resumeMapper;

    @Mock
    private UserProgressService userProgressService;

    @Mock
    private StorageService storageService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private User user;
    private Resume resume;
    private ResumeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("student@example.com")
                .fullName("Student Name")
                .build();

        resume = Resume.builder()
                .id(2L)
                .fileUrl("http://example.com/resume.pdf")
                .uploadedAt(LocalDateTime.now())
                .user(user)
                .build();

        responseDTO = ResumeResponseDTO.builder()
                .id(2L)
                .fileUrl("http://example.com/resume.pdf")
                .uploadedAt(resume.getUploadedAt())
                .userId(1L)
                .build();
    }

    @Test
    void uploadAndAnalyze_Success() {
        ResumeRequestDTO requestDTO = ResumeRequestDTO.builder()
                .fileUrl("http://example.com/resume.pdf")
                .build();

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(resumeRepository.save(any(Resume.class))).thenReturn(resume);
        when(resumeAnalysisRepository.save(any(ResumeAnalysis.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(userProgressService).updateProgressAfterResume(any(User.class), anyInt());
        when(resumeMapper.toResponseDTO(resume)).thenReturn(responseDTO);

        ApiResponse<ResumeResponseDTO> response = resumeService.uploadAndAnalyze("student@example.com", requestDTO);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("http://example.com/resume.pdf", response.getData().getFileUrl());
        verify(userProgressService, times(1)).updateProgressAfterResume(eq(user), anyInt());
    }
}
