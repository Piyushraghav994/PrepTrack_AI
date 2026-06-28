package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.*;

import java.util.List;

/**
 * Service interface for Interview Session and Feedback operations.
 */
public interface InterviewSessionService {

    ApiResponse<InterviewSessionResponseDTO> startSession(String email, Long interviewId);

    ApiResponse<InterviewSessionResponseDTO> submitSession(Long sessionId, SubmitSessionRequestDTO request);

    ApiResponse<List<InterviewSessionResponseDTO>> getUserSessions(String email);

    ApiResponse<InterviewFeedbackResponseDTO> getSessionFeedback(Long sessionId);
}
