package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing an Interview Feedback response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewFeedbackResponseDTO {
    private Long id;
    private Long sessionId;
    private String strengths;
    private String weaknesses;
    private String recommendations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
