package com.PrepTrack_AI.Fullstack_Project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new Permission.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRequest {

    @NotBlank(message = "Permission name is required")
    private String name;
}
