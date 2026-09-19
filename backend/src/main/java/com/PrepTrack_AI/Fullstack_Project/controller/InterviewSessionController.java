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
@RequestMapping({"/api/interview-sessions", "/api/sessions"})
@RequiredArgsConstructor
@Tag(name = "Interview Session Management", description = "Endpoints for starting, submitting, and viewing interview sessions and feedback")
public class InterviewSessionController {

    private final InterviewSessionService interviewSessionService;

    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Start an Interview Session", description = "Starts a new mock interview session for the authenticated user.")
    public ResponseEntity<ApiResponse<InterviewSessionResponseDTO>> startSession(
            @RequestParam(required = false) Long interviewId,
            @RequestBody(required = false) java.util.Map<String, Object> body,
            Principal principal) {
        Long resolvedInterviewId = interviewId;
        if (resolvedInterviewId == null && body != null && body.containsKey("interviewId")) {
            Object idObj = body.get("interviewId");
            if (idObj instanceof Number number) {
                resolvedInterviewId = number.longValue();
            } else if (idObj != null) {
                resolvedInterviewId = Long.parseLong(idObj.toString());
            }
        }
        if (resolvedInterviewId == null) {
            throw new IllegalArgumentException("interviewId is required either as a query parameter or in request body");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewSessionService.startSession(principal.getName(), resolvedInterviewId));
    }

    @PostMapping(value = {"/{sessionId}/submit", "/submit"})
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit an Interview Session", description = "Submits scores and completes an interview session, triggering feedback generation.")
    public ResponseEntity<ApiResponse<InterviewSessionResponseDTO>> submitSession(
            @PathVariable(required = false) Long sessionId,
            @RequestBody java.util.Map<String, Object> body) {
        Long resolvedSessionId = sessionId;
        if (resolvedSessionId == null && body.containsKey("sessionId")) {
            Object idObj = body.get("sessionId");
            if (idObj instanceof Number number) {
                resolvedSessionId = number.longValue();
            } else if (idObj != null) {
                resolvedSessionId = Long.parseLong(idObj.toString());
            }
        }
        if (resolvedSessionId == null) {
            throw new IllegalArgumentException("sessionId is required either in URL path or request body");
        }

        Integer score = 80; // default score
        if (body.containsKey("score") && body.get("score") != null) {
            Object sObj = body.get("score");
            if (sObj instanceof Number number) {
                score = number.intValue();
            } else {
                score = Integer.parseInt(sObj.toString());
            }
        }

        SubmitSessionRequestDTO requestDTO = SubmitSessionRequestDTO.builder()
                .score(score)
                .build();

        return ResponseEntity.ok(interviewSessionService.submitSession(resolvedSessionId, requestDTO));
    }

    @GetMapping({"/user", "/my", ""})
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's Interview Sessions", description = "Retrieves all mock interview sessions for the authenticated user.")
    public ResponseEntity<ApiResponse<PagedResponse<InterviewSessionResponseDTO>>> getUserSessions(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(interviewSessionService.getUserSessions(principal.getName(), page, size));
    }

    @GetMapping({"/{sessionId}/feedback", "/{sessionId}"})
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Session Feedback", description = "Retrieves evaluation feedback for a completed interview session.")
    public ResponseEntity<ApiResponse<InterviewFeedbackResponseDTO>> getSessionFeedback(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(interviewSessionService.getSessionFeedback(sessionId));
    }
}
