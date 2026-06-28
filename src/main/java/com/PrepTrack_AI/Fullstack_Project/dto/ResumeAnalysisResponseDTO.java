package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a Resume Analysis response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysisResponseDTO {
    private Long id;
    private Long resumeId;
    private Integer atsScore;
    private String skills;
    private String missingKeywords;
    private String suggestions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
