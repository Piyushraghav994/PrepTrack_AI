package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing an Interview Question response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestionResponseDTO {
    private Long id;
    private String question;
    private String answer;
    private String topic;
    private String category;
    private Long interviewId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
