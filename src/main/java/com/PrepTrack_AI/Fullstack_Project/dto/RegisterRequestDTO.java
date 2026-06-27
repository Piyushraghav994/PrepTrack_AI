package com.PrepTrack_AI.Fullstack_Project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;

    private String college;

    private String branch;

    @NotNull(message = "Passout year is required")
    @Min(value = 2000, message = "Passout year must be 2000 or later")
    @Max(value = 2035, message = "Passout year must be 2035 or earlier")
    private Integer passoutYear;
}
