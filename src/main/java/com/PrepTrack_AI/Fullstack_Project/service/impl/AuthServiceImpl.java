package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.entity.*;
import com.PrepTrack_AI.Fullstack_Project.exception.*;
import com.PrepTrack_AI.Fullstack_Project.repository.*;
import com.PrepTrack_AI.Fullstack_Project.security.CustomUserDetails;
import com.PrepTrack_AI.Fullstack_Project.security.JwtService;
import com.PrepTrack_AI.Fullstack_Project.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

/**
 * Service implementation for authentication and token management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RevokedTokenRepository revokedTokenRepository;

    // ── Register ──────────────────────────────────────────────────────────────

    @Override
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Guard: duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        // Fetch ROLE_STUDENT
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_STUDENT"));

        // Build User entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .college(request.getCollege())
                .branch(request.getBranch())
                .passoutYear(request.getPassoutYear())
                .role(studentRole)
                .emailVerified(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        user.setCreatedBy("SYSTEM");
        user.setUpdatedBy("SYSTEM");

        user = userRepository.save(user);
        log.info("User registered successfully: id={}, email={}", user.getId(), user.getEmail());

        // Generate email verification token
        String verificationTokenValue = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(verificationTokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        emailVerificationTokenRepository.save(verificationToken);

        // Simulation: Log verification token to console for local testing
        log.info("=========================================================================");
        log.info("EMAIL VERIFICATION TOKEN FOR {}: {}", user.getEmail(), verificationTokenValue);
        log.info("=========================================================================");

        // Generate tokens for response
        String accessToken = jwtService.generateToken(new CustomUserDetails(user));
        String refreshTokenValue = createRefreshToken(user);

        return ApiResponse.success(
                "User registered successfully. Please verify your email.",
                buildAuthResponse(user, accessToken, refreshTokenValue)
        );
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Override
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Authenticate credentials
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        // Invalidate older refresh tokens and generate a new one
        refreshTokenRepository.deleteByUser(user);
        String refreshTokenValue = createRefreshToken(user);

        // Update login tracking
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(new CustomUserDetails(user));
        log.info("Login successful for user: {}", user.getEmail());

        return ApiResponse.success(
                "Login successful",
                buildAuthResponse(user, accessToken, refreshTokenValue)
        );
    }

    // ── Forgot Password ───────────────────────────────────────────────────────

    @Override
    public ApiResponse<Void> forgotPassword(ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        // Delete existing reset tokens
        passwordResetTokenRepository.deleteByUser(user);

        // Generate secure reset token (15 mins expiry)
        String resetTokenValue = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(resetTokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        passwordResetTokenRepository.save(resetToken);

        // Simulation: Log reset token to console for local testing
        log.info("=========================================================================");
        log.info("PASSWORD RESET TOKEN FOR {}: {}", user.getEmail(), resetTokenValue);
        log.info("=========================================================================");

        return ApiResponse.success("Password reset token generated and logged to console.");
    }

    // ── Reset Password ────────────────────────────────────────────────────────

    @Override
    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        log.info("Attempting to reset password using token...");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Clean up used token
        passwordResetTokenRepository.delete(resetToken);
        log.info("Password reset successfully for user: {}", user.getEmail());

        return ApiResponse.success("Password has been reset successfully.");
    }

    // ── Change Password ───────────────────────────────────────────────────────

    @Override
    public ApiResponse<Void> changePassword(String email, ChangePasswordRequest request) {
        log.info("Change password request for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", email);

        return ApiResponse.success("Password changed successfully.");
    }

    // ── Refresh Token ─────────────────────────────────────────────────────────

    @Override
    public ApiResponse<TokenRefreshResponse> refreshToken(RefreshTokenRequest request) {
        log.info("Token refresh request received");

        RefreshToken oldToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (oldToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(oldToken);
            throw new TokenExpiredException("Refresh token has expired. Please login again.");
        }

        User user = oldToken.getUser();

        // Invalidate old refresh token and rotate with a new one
        refreshTokenRepository.delete(oldToken);
        String newRefreshTokenValue = createRefreshToken(user);

        // Generate new short-lived access token
        String newAccessToken = jwtService.generateToken(new CustomUserDetails(user));
        log.info("Refresh token rotated successfully for user: {}", user.getEmail());

        TokenRefreshResponse response = TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenValue)
                .build();

        return ApiResponse.success("Tokens refreshed successfully", response);
    }

    @Override
    public ApiResponse<Void> logout(String refreshToken, String authHeader) {
        log.info("Logging out user token");
        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                java.util.Date expirationDate = jwtService.extractExpiration(jwt);
                java.time.LocalDateTime expiryLdt = expirationDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();

                RevokedToken revokedToken = RevokedToken.builder()
                        .token(jwt)
                        .expiryDate(expiryLdt)
                        .build();
                revokedToken.setCreatedBy("SYSTEM");
                revokedToken.setUpdatedBy("SYSTEM");
                revokedTokenRepository.save(revokedToken);
                log.info("Access token blacklisted successfully");
            } catch (Exception e) {
                log.warn("Failed to extract expiration or blacklist token: {}", e.getMessage());
            }
        }
        return ApiResponse.success("Logged out successfully");
    }

    // ── Verify Email ──────────────────────────────────────────────────────────

    @Override
    public ApiResponse<Void> verifyEmail(VerifyEmailRequest request) {
        log.info("Verifying user email using token...");

        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            emailVerificationTokenRepository.delete(verificationToken);
            throw new TokenExpiredException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Clean up token
        emailVerificationTokenRepository.delete(verificationToken);
        log.info("Email verified successfully for user: {}", user.getEmail());

        return ApiResponse.success("Email verified successfully.");
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private String createRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(30)) // Valid for 30 days
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .token(accessToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .refreshToken(refreshToken)
                .build();
    }
}
