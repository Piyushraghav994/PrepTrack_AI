package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.entity.Difficulty;

import java.util.List;

/**
 * Service interface for Interview and Interview Question management.
 */
public interface InterviewService {

    ApiResponse<InterviewResponseDTO> createInterview(InterviewRequestDTO request);

    ApiResponse<InterviewResponseDTO> getInterviewById(Long id);

    ApiResponse<PagedResponse<InterviewResponseDTO>> getAllInterviews(Difficulty difficulty, String role, int page, int size);

    ApiResponse<InterviewResponseDTO> updateInterview(Long id, InterviewRequestDTO request);

    ApiResponse<Void> deleteInterview(Long id);

    ApiResponse<InterviewQuestionResponseDTO> addQuestionToInterview(InterviewQuestionRequestDTO request);

    ApiResponse<PagedResponse<InterviewQuestionResponseDTO>> getQuestionsByInterview(Long interviewId, int page, int size);

    ApiResponse<InterviewQuestionResponseDTO> updateQuestion(Long questionId, InterviewQuestionRequestDTO request);

    ApiResponse<Void> deleteQuestion(Long questionId);
}
