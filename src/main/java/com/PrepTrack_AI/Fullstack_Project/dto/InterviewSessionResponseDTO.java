package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing an Interview Session response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSessionResponseDTO {
    private Long id;
    private Long userId;
    private Long interviewId;
    private String interviewTitle;
    private Integer score;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
