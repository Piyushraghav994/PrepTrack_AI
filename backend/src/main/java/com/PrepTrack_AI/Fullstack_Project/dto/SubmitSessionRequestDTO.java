package com.PrepTrack_AI.Fullstack_Project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for submitting an Interview Session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitSessionRequestDTO {
    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score cannot be less than 0")
    @Max(value = 100, message = "Score cannot be more than 100")
    private Integer score;
}
