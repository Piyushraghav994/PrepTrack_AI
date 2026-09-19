package com.PrepTrack_AI.Fullstack_Project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating an Interview Question.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestionRequestDTO {
    @NotBlank(message = "Question text is required")
    private String question;

    @NotBlank(message = "Answer text is required")
    private String answer;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Interview ID is required")
    private Long interviewId;
}
