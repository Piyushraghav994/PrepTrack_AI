package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.entity.Difficulty;
import com.PrepTrack_AI.Fullstack_Project.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Interviews and Interview Questions.
 */
@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Tag(name = "Interview Management", description = "Endpoints for administering and viewing interviews/questions")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create an Interview (Admin)", description = "Creates a new interview record. Admin only.")
    public ResponseEntity<ApiResponse<InterviewResponseDTO>> createInterview(
            @Valid @RequestBody InterviewRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewService.createInterview(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Interview by ID", description = "Retrieves details of a specific interview.")
    public ResponseEntity<ApiResponse<InterviewResponseDTO>> getInterviewById(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.getInterviewById(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all Interviews", description = "Retrieves all interviews, optionally filtered by difficulty or role.")
    public ResponseEntity<ApiResponse<PagedResponse<InterviewResponseDTO>>> getAllInterviews(
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(interviewService.getAllInterviews(difficulty, role, page, size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update an Interview (Admin)", description = "Updates details of an existing interview. Admin only.")
    public ResponseEntity<ApiResponse<InterviewResponseDTO>> updateInterview(
            @PathVariable Long id,
            @Valid @RequestBody InterviewRequestDTO request) {
        return ResponseEntity.ok(interviewService.updateInterview(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete an Interview (Admin)", description = "Deletes an existing interview. Admin only.")
    public ResponseEntity<ApiResponse<Void>> deleteInterview(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.deleteInterview(id));
    }

    // ─── Question Endpoints ──────────────────────────────────────────────────

    @PostMapping("/questions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Add Question to Interview (Admin)", description = "Adds a question to a specific interview. Admin only.")
    public ResponseEntity<ApiResponse<InterviewQuestionResponseDTO>> addQuestion(
            @Valid @RequestBody InterviewQuestionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewService.addQuestionToInterview(request));
    }

    @GetMapping("/{id}/questions")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get questions for an Interview", description = "Retrieves all questions associated with an interview.")
    public ResponseEntity<ApiResponse<PagedResponse<InterviewQuestionResponseDTO>>> getQuestionsByInterview(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(interviewService.getQuestionsByInterview(id, page, size));
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a Question (Admin)", description = "Updates details of an existing interview question. Admin only.")
    public ResponseEntity<ApiResponse<InterviewQuestionResponseDTO>> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody InterviewQuestionRequestDTO request) {
        return ResponseEntity.ok(interviewService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a Question (Admin)", description = "Deletes a question from an interview. Admin only.")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(interviewService.deleteQuestion(questionId));
    }
}
