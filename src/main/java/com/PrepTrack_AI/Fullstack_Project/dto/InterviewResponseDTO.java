package com.PrepTrack_AI.Fullstack_Project.dto;

import com.PrepTrack_AI.Fullstack_Project.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing an Interview response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResponseDTO {
    private Long id;
    private String title;
    private String company;
    private String role;
    private Difficulty difficulty;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
