package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeAnalysisResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;
import com.PrepTrack_AI.Fullstack_Project.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;

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

    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload and Analyze Resume File", description = "Uploads a raw resume file (PDF/Doc) and runs mock ATS analysis.")
    public ResponseEntity<ApiResponse<ResumeResponseDTO>> uploadResumeFile(
            @RequestParam("file") MultipartFile file,
            Principal principal) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeService.uploadAndAnalyzeFile(principal.getName(), file));
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's Resumes", description = "Retrieves all resumes uploaded by the authenticated user.")
    public ResponseEntity<ApiResponse<PagedResponse<ResumeResponseDTO>>> getUserResumes(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(resumeService.getUserResumes(principal.getName(), page, size));
    }

    @GetMapping("/{resumeId}/analysis")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Resume ATS Analysis", description = "Retrieves ATS analysis parsing score and feedback for a resume.")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponseDTO>> getResumeAnalysis(
            @PathVariable Long resumeId) {
        return ResponseEntity.ok(resumeService.getResumeAnalysis(resumeId));
    }
}
