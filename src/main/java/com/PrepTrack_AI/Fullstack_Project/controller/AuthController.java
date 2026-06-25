package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST controller for authentication and user verification endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for Registration, Login, Session Management, and Verification")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user and returns access and refresh tokens.
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new STUDENT account and returns access and refresh tokens."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    /**
     * Authenticates an existing user and returns access and refresh tokens.
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login with existing credentials",
            description = "Authenticates the user and returns access and refresh tokens."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Initiates the forgot password process.
     */
    @PostMapping("/forgot-password")
    @Operation(
            summary = "Forgot Password",
            description = "Generates a reset password token and outputs it to the backend console."
    )
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    /**
     * Completes password reset using verification token.
     */
    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset Password",
            description = "Resets the user's password using the generated token."
    )
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(authService.resetPassword(request));
    }

    /**
     * Updates user's password. Requires valid authorization token in headers.
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Change Password",
            description = "Allows an authenticated user to change their current password."
    )
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Principal principal) {

        return ResponseEntity.ok(authService.changePassword(principal.getName(), request));
    }

    /**
     * Refreshes expired access tokens.
     */
    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh Token",
            description = "Rotates and issues a new access token and refresh token."
    )
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(authService.refreshToken(request));
    }

    /**
     * Invalidates refresh tokens.
     */
    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Signs out the user by deleting their database-backed refresh token and blacklisting their access token."
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        return ResponseEntity.ok(authService.logout(request.getRefreshToken(), authHeader));
    }

    /**
     * Verifies user email.
     */
    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify Email",
            description = "Verifies the student's email using the token outputted to the backend console."
    )
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {

        return ResponseEntity.ok(authService.verifyEmail(request));
    }
}
