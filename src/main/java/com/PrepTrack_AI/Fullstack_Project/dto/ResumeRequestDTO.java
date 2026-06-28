package com.PrepTrack_AI.Fullstack_Project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for uploading a Resume.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeRequestDTO {
    @NotBlank(message = "File URL is required")
    private String fileUrl;
}
