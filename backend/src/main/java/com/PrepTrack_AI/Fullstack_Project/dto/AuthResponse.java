package com.PrepTrack_AI.Fullstack_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned on successful register or login.
 * Contains the JWT Bearer token and basic user identity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /** JWT access token the client must include in all protected requests. */
    private String token;

    /** Always {@code "Bearer"} — tells the client how to use the token. */
    @Builder.Default
    private String tokenType = "Bearer";

    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String refreshToken;
}
