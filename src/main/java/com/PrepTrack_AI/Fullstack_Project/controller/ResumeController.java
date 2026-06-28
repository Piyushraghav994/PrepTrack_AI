package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeAnalysisResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.service.ResumeService;
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
 * REST controller for Resume uploading and ATS parsing analysis.
 */
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Analysis", description = "Endpoints for uploading resumes and retrieving ATS analysis results")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload and Analyze Resume", description = "Uploads a resume file URL and runs mock ATS analysis.")
    public ResponseEntity<ApiResponse<ResumeResponseDTO>> uploadResume(
            @Valid @RequestBody ResumeRequestDTO request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeService.uploadAndAnalyze(principal.getName(), request));
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's Resumes", description = "Retrieves all resumes uploaded by the authenticated user.")
    public ResponseEntity<ApiResponse<List<ResumeResponseDTO>>> getUserResumes(Principal principal) {
        return ResponseEntity.ok(resumeService.getUserResumes(principal.getName()));
    }

    @GetMapping("/{resumeId}/analysis")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Resume ATS Analysis", description = "Retrieves ATS analysis parsing score and feedback for a resume.")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponseDTO>> getResumeAnalysis(
            @PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeService.getResumeAnalysis(resumeId));
    }
}
