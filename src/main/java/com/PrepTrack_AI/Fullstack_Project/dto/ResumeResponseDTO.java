package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a Resume response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeResponseDTO {
    private Long id;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private Long userId;
}
