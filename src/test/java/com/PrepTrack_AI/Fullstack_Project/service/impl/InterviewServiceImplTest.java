package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.entity.Difficulty;
import com.PrepTrack_AI.Fullstack_Project.entity.Interview;
import com.PrepTrack_AI.Fullstack_Project.entity.InterviewQuestion;
import com.PrepTrack_AI.Fullstack_Project.mapper.InterviewMapper;
import com.PrepTrack_AI.Fullstack_Project.mapper.InterviewQuestionMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewQuestionRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceImplTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private InterviewQuestionRepository interviewQuestionRepository;

    @Mock
    private InterviewMapper interviewMapper;

    @Mock
    private InterviewQuestionMapper interviewQuestionMapper;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    private Interview interview;
    private InterviewRequestDTO requestDTO;
    private InterviewResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        interview = Interview.builder()
                .id(1L)
                .title("Java Interview")
                .company("Google")
                .role("Software Engineer")
                .difficulty(Difficulty.MEDIUM)
                .description("Java programming and architecture")
                .build();

        requestDTO = InterviewRequestDTO.builder()
                .title("Java Interview")
                .company("Google")
                .role("Software Engineer")
                .difficulty(Difficulty.MEDIUM)
                .description("Java programming and architecture")
                .build();

        responseDTO = InterviewResponseDTO.builder()
                .id(1L)
                .title("Java Interview")
                .company("Google")
                .role("Software Engineer")
                .difficulty(Difficulty.MEDIUM)
                .description("Java programming and architecture")
                .build();
    }

    @Test
    void createInterview_Success() {
        when(interviewMapper.toEntity(requestDTO)).thenReturn(interview);
        when(interviewRepository.save(interview)).thenReturn(interview);
        when(interviewMapper.toResponseDTO(interview)).thenReturn(responseDTO);

        ApiResponse<InterviewResponseDTO> response = interviewService.createInterview(requestDTO);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Java Interview", response.getData().getTitle());
        verify(interviewRepository, times(1)).save(interview);
    }

    @Test
    void getInterviewById_Success() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewMapper.toResponseDTO(interview)).thenReturn(responseDTO);

        ApiResponse<InterviewResponseDTO> response = interviewService.getInterviewById(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(1L, response.getData().getId());
    }

    @Test
    void addQuestionToInterview_Success() {
        InterviewQuestionRequestDTO questionReq = InterviewQuestionRequestDTO.builder()
                .interviewId(1L)
                .question("What is JVM?")
                .answer("Java Virtual Machine")
                .topic("Java")
                .category("Core")
                .build();

        InterviewQuestion question = InterviewQuestion.builder()
                .id(10L)
                .question("What is JVM?")
                .answer("Java Virtual Machine")
                .topic("Java")
                .category("Core")
                .interview(interview)
                .build();

        InterviewQuestionResponseDTO questionRes = InterviewQuestionResponseDTO.builder()
                .id(10L)
                .question("What is JVM?")
                .answer("Java Virtual Machine")
                .topic("Java")
                .category("Core")
                .interviewId(1L)
                .build();

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewQuestionMapper.toEntity(questionReq)).thenReturn(question);
        when(interviewQuestionRepository.save(question)).thenReturn(question);
        when(interviewQuestionMapper.toResponseDTO(question)).thenReturn(questionRes);

        ApiResponse<InterviewQuestionResponseDTO> response = interviewService.addQuestionToInterview(questionReq);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(10L, response.getData().getId());
        assertEquals("What is JVM?", response.getData().getQuestion());
    }
}
