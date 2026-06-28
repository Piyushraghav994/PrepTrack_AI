package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing User Progress response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressResponseDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private Integer completedQuestions;
    private Integer completedInterviews;
    private Integer currentStreak;
    private Integer totalScore;
    private LocalDateTime updatedAt;
}
