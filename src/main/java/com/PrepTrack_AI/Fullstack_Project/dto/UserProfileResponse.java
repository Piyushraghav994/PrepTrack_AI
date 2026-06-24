package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for exposing a user profile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    
    // Profile information
    private String college;
    private String branch;
    private Integer passoutYear;
    private String profileImageUrl;
    private String phoneNumber;
    private String linkedinUrl;
    private String githubUrl;
    private String resumeUrl;

    // Security status
    private Boolean emailVerified;
    private Boolean accountNonLocked;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private Boolean enabled;

    // Login tracking
    private LocalDateTime lastLoginAt;
    private String lastLoginIp;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
