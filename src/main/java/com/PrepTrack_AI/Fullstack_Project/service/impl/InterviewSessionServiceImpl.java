package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.entity.*;
import com.PrepTrack_AI.Fullstack_Project.service.NotificationService;
import com.PrepTrack_AI.Fullstack_Project.entity.NotificationType;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.exception.UserNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.mapper.InterviewSessionMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewFeedbackRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewSessionRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.InterviewSessionService;
import com.PrepTrack_AI.Fullstack_Project.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the {@link InterviewSessionService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InterviewSessionServiceImpl implements InterviewSessionService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewFeedbackRepository interviewFeedbackRepository;
    private final InterviewSessionMapper interviewSessionMapper;
    private final UserProgressService userProgressService;
    private final NotificationService notificationService;

    @Override
    public ApiResponse<InterviewSessionResponseDTO> startSession(String email, Long interviewId) {
        log.info("Starting interview session for user: {} on interview: {}", email, interviewId);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", interviewId));

        InterviewSession session = InterviewSession.builder()
                .user(user)
                .interview(interview)
                .startTime(LocalDateTime.now())
                .build();

        InterviewSession saved = interviewSessionRepository.save(session);
        log.info("Interview session started with ID: {}", saved.getId());
        return ApiResponse.success("Interview session started successfully", interviewSessionMapper.toResponseDTO(saved));
    }

    @Override
    public ApiResponse<InterviewSessionResponseDTO> submitSession(Long sessionId, SubmitSessionRequestDTO request) {
        log.info("Submitting interview session with ID: {}, score: {}", sessionId, request.getScore());
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewSession", "id", sessionId));

        if (session.getEndTime() != null) {
            throw new IllegalStateException("Session has already been submitted and completed");
        }

        session.setEndTime(LocalDateTime.now());
        session.setScore(request.getScore());
        InterviewSession updated = interviewSessionRepository.save(session);

        // Generate mock feedback based on score
        InterviewFeedback feedback = generateFeedbackForSession(updated);
        interviewFeedbackRepository.save(feedback);

        // Update User Progress
        int questionsCount = updated.getInterview().getQuestions() != null ? updated.getInterview().getQuestions().size() : 5;
        if (questionsCount == 0) {
            questionsCount = 5; // default fallback
        }
        userProgressService.updateProgressAfterSession(updated.getUser(), request.getScore(), questionsCount);

        notificationService.sendNotification(updated.getUser(), "Interview Session Completed", 
                "You completed your mock interview for " + updated.getInterview().getRole() + " at " + updated.getInterview().getCompany() + " with a score of " + updated.getScore() + "/100.", NotificationType.SUCCESS);

        log.info("Interview session ID {} submitted successfully", sessionId);
        return ApiResponse.success("Interview session submitted successfully", interviewSessionMapper.toResponseDTO(updated));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<InterviewSessionResponseDTO>> getUserSessions(String email, int page, int size) {
        log.debug("Fetching interview sessions for user: {}, page: {}, size: {}", email, page, size);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Pageable pageable = PageRequest.of(page, size);
        Page<InterviewSession> sessionPage = interviewSessionRepository.findByUserIdOrderByStartTimeDesc(user.getId(), pageable);
        List<InterviewSessionResponseDTO> content = interviewSessionMapper.toResponseDTOList(sessionPage.getContent());

        PagedResponse<InterviewSessionResponseDTO> response = PagedResponse.<InterviewSessionResponseDTO>builder()
                .content(content)
                .pageNumber(sessionPage.getNumber())
                .pageSize(sessionPage.getSize())
                .totalElements(sessionPage.getTotalElements())
                .totalPages(sessionPage.getTotalPages())
                .last(sessionPage.isLast())
                .build();

        return ApiResponse.success("User sessions retrieved successfully", response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<InterviewFeedbackResponseDTO> getSessionFeedback(Long sessionId) {
        log.debug("Fetching feedback for session ID: {}", sessionId);
        if (!interviewSessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("InterviewSession", "id", sessionId);
        }

        InterviewFeedback feedback = interviewFeedbackRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewFeedback not found for session ID: " + sessionId));

        return ApiResponse.success("Feedback retrieved successfully", interviewSessionMapper.toFeedbackResponseDTO(feedback));
    }

    private InterviewFeedback generateFeedbackForSession(InterviewSession session) {
        int score = session.getScore();
        String role = session.getInterview().getRole();
        String company = session.getInterview().getCompany();

        String strengths;
        String weaknesses;
        String recommendations;

        if (score >= 80) {
            strengths = "Excellent technical knowledge! Demonstrated deep understanding of " + role + " principles. " +
                    "Excellent problem-solving approach and code structuring matches standards at " + company + ".";
            weaknesses = "Minor edge-case coverage and low-level code optimizations could be slightly improved. " +
                    "Could expand a bit more on complex trade-offs during architectural decisions.";
            recommendations = "Keep up the fantastic work! Try harder and more challenging system design mock interviews. " +
                    "Focus on explaining latency, scalability, and high-availability tradeoffs during your interviews.";
        } else if (score >= 50) {
            strengths = "Good core understanding of " + role + " requirements. Answered standard algorithmic and theoretical questions " +
                    "correctly. Clear communication during problem solving.";
            weaknesses = "Lacked depth in advanced topics (e.g. multi-threading, memory optimization, or caching). " +
                    "Struggled to optimize the initial brute-force approach efficiently.";
            recommendations = "Revise key intermediate and advanced concepts related to " + role + ". " +
                    "Practice writing code for Medium difficulty problems on platforms like LeetCode. Review " + company + " specific interview patterns.";
        } else {
            strengths = "Able to explain basic concepts and flow of the problem. Showed willingness to accept hints and improve.";
            weaknesses = "Struggled with core programming logic and syntax. Had difficulty identifying correct data structures. " +
                    "Foundational understanding of " + role + " was limited.";
            recommendations = "Spend time strengthening your core foundations first. Re-read standard guides on data structures, " +
                    "algorithms, and object-oriented principles. Work through Easy-level challenges before retaking the mock interview.";
        }

        return InterviewFeedback.builder()
                .session(session)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .recommendations(recommendations)
                .build();
    }
}
