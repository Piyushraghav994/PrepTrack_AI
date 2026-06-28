package com.PrepTrack_AI.Fullstack_Project.dto;

import com.PrepTrack_AI.Fullstack_Project.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating an Interview.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Role is required")
    private String role;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    private String description;
}
