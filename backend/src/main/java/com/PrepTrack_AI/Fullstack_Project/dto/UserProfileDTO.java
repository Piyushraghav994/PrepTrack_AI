package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    
    private String college;
    private String branch;
    private Integer passoutYear;
    private String profileImageUrl;
    private String phoneNumber;
    private String linkedinUrl;
    private String githubUrl;
    private String resumeUrl;

    private Boolean emailVerified;
    private Boolean accountNonLocked;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private Boolean enabled;

    private LocalDateTime lastLoginAt;
    private String lastLoginIp;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
