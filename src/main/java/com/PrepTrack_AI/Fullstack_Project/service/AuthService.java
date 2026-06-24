package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.*;

/**
 * Service interface defining the authentication contract for PrepTrack.
 *
 * <p>Implementations live in {@code service.impl} package.</p>
 */
public interface AuthService {

    /**
     * Registers a new user account.
     *
     * @param request validated registration payload
     * @return {@link ApiResponse} wrapping an {@link AuthResponse} with a JWT token
     * @throws com.PrepTrack_AI.Fullstack_Project.exception.DuplicateResourceException if email is already registered
     */
    ApiResponse<AuthResponse> register(RegisterRequest request);

    /**
     * Authenticates an existing user with their credentials.
     *
     * @param request validated login payload
     * @return {@link ApiResponse} wrapping an {@link AuthResponse} with a JWT token
     * @throws org.springframework.security.authentication.BadCredentialsException on invalid credentials
     */
    ApiResponse<AuthResponse> login(LoginRequest request);

    /**
     * Initiates the forgot password workflow by generating and logging a reset token.
     */
    ApiResponse<Void> forgotPassword(ForgotPasswordRequest request);

    /**
     * Resets the password using a valid, non-expired reset token.
     */
    ApiResponse<Void> resetPassword(ResetPasswordRequest request);

    /**
     * Changes password for an authenticated user.
     */
    ApiResponse<Void> changePassword(String email, ChangePasswordRequest request);

    /**
     * Refreshes the short-lived JWT access token using a valid database-backed refresh token.
     */
    ApiResponse<TokenRefreshResponse> refreshToken(RefreshTokenRequest request);

    /**
     * Logs out the user by deleting their database-backed refresh token.
     */
    ApiResponse<Void> logout(String refreshToken);

    /**
     * Verifies the student email using a valid, non-expired verification token.
     */
    ApiResponse<Void> verifyEmail(VerifyEmailRequest request);
}
