package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProgressResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.service.UserProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * REST controller for retrieving User Progress details.
 */
@RestController
@RequestMapping("/api/user-progress")
@RequiredArgsConstructor
@Tag(name = "User Progress Tracking", description = "Endpoints for tracking user interview scores, streak, and completed questions")
public class UserProgressController {

    private final UserProgressService userProgressService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user progress", description = "Retrieves streak, score, completed interviews, and completed questions of the authenticated user.")
    public ResponseEntity<ApiResponse<UserProgressResponseDTO>> getProgress(Principal principal) {
        return ResponseEntity.ok(userProgressService.getUserProgress(principal.getName()));
    }
}
