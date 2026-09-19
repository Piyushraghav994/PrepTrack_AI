package com.PrepTrack_AI.Fullstack_Project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Size(max = 100, message = "College name must not exceed 100 characters")
    private String college;

    @Size(max = 100, message = "Branch name must not exceed 100 characters")
    private String branch;

    @NotNull(message = "Passout year is required")
    @Min(value = 2000, message = "Passout year must be 2000 or later")
    @Max(value = 2035, message = "Passout year must be 2035 or earlier")
    private Integer passoutYear;

    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImageUrl;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 250, message = "LinkedIn URL must not exceed 250 characters")
    private String linkedinUrl;

    @Size(max = 250, message = "GitHub URL must not exceed 250 characters")
    private String githubUrl;

    @Size(max = 500, message = "Resume URL must not exceed 500 characters")
    private String resumeUrl;
}
