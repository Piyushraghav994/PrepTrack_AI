package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller exposing general file upload operations using the active cloud storage provider.
 */
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Tag(name = "Cloud Storage Uploads", description = "Endpoints for uploading raw files directly to AWS S3, Cloudinary, or MinIO")
@Slf4j
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload/resume")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload raw Resume file", description = "Uploads a resume file using the active storage provider and returns its URL.")
    public ResponseEntity<ApiResponse<String>> uploadResume(
            @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Request to upload raw resume file: name={}", file.getOriginalFilename());
        String url = storageService.uploadFile(file, "resumes");
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", url));
    }

    @PostMapping("/upload/profile-picture")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload raw Profile Picture", description = "Uploads a profile picture image using the active storage provider and returns its URL.")
    public ResponseEntity<ApiResponse<String>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Request to upload raw profile image: name={}", file.getOriginalFilename());
        String url = storageService.uploadFile(file, "profiles");
        return ResponseEntity.ok(ApiResponse.success("Profile picture uploaded successfully", url));
    }
}
