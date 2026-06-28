package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.entity.RefreshToken;
import com.PrepTrack_AI.Fullstack_Project.entity.RevokedToken;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.repository.RefreshTokenRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.RevokedTokenRepository;
import com.PrepTrack_AI.Fullstack_Project.mapper.AuthMapper;
import com.PrepTrack_AI.Fullstack_Project.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private String validRefreshToken;
    private String jwtToken;
    private String authHeader;
    private Date jwtExpiryDate;

    @BeforeEach
    void setUp() {
        validRefreshToken = "valid-refresh-token";
        jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiZXhwIjoxNzE5Mzk5ODk5fQ";
        authHeader = "Bearer " + jwtToken;
        jwtExpiryDate = new Date(System.currentTimeMillis() + 900000); // 15 mins from now
    }

    @Test
    void logout_Success_DeletesRefreshAndBlacklistsAccess() {
        when(jwtService.extractExpiration(jwtToken)).thenReturn(jwtExpiryDate);

        ApiResponse<Void> response = authService.logout(validRefreshToken, authHeader);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Logged out successfully", response.getMessage());

        // Verify refresh token deletion
        verify(refreshTokenRepository, times(1)).deleteByToken(validRefreshToken);

        // Verify access token revocation save
        ArgumentCaptor<RevokedToken> captor = ArgumentCaptor.forClass(RevokedToken.class);
        verify(revokedTokenRepository, times(1)).save(captor.capture());
        RevokedToken captured = captor.getValue();
        assertEquals(jwtToken, captured.getToken());
        
        LocalDateTime expectedLdt = jwtExpiryDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        // Since milliseconds might vary, check it's within 1 second
        assertTrue(captured.getExpiryDate().isBefore(expectedLdt.plusSeconds(1)) &&
                captured.getExpiryDate().isAfter(expectedLdt.minusSeconds(1)));
    }

    @Test
    void logout_Success_NoAuthHeader() {
        ApiResponse<Void> response = authService.logout(validRefreshToken, null);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        verify(refreshTokenRepository, times(1)).deleteByToken(validRefreshToken);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(revokedTokenRepository);
    }
}
