package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.service.InterviewSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for managing Interview Sessions and Session Feedback.
 */
@RestController
@RequestMapping("/api/interview-sessions")
@RequiredArgsConstructor
@Tag(name = "Interview Session Management", description = "Endpoints for starting, submitting, and viewing interview sessions and feedback")
public class InterviewSessionController {

    private final InterviewSessionService interviewSessionService;

    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Start an Interview Session", description = "Starts a new mock interview session for the authenticated user.")
    public ResponseEntity<ApiResponse<InterviewSessionResponseDTO>> startSession(
            @RequestParam Long interviewId,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewSessionService.startSession(principal.getName(), interviewId));
    }

    @PostMapping("/{sessionId}/submit")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit an Interview Session", description = "Submits scores and completes an interview session, triggering feedback generation.")
    public ResponseEntity<ApiResponse<InterviewSessionResponseDTO>> submitSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody SubmitSessionRequestDTO request) {
        return ResponseEntity.ok(interviewSessionService.submitSession(sessionId, request));
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's Interview Sessions", description = "Retrieves all mock interview sessions for the authenticated user.")
    public ResponseEntity<ApiResponse<List<InterviewSessionResponseDTO>>> getUserSessions(Principal principal) {
        return ResponseEntity.ok(interviewSessionService.getUserSessions(principal.getName()));
    }

    @GetMapping("/{sessionId}/feedback")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Session Feedback", description = "Retrieves evaluation feedback for a completed interview session.")
    public ResponseEntity<ApiResponse<InterviewFeedbackResponseDTO>> getSessionFeedback(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(interviewSessionService.getSessionFeedback(sessionId));
    }
}
