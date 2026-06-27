package com.PrepTrack_AI.Fullstack_Project.security;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.repository.RevokedTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that intercepts every HTTP request exactly once.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Skip filter if header is absent or not a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);  // strip "Bearer " prefix

        try {
            // Check if token is blacklisted/revoked
            if (revokedTokenRepository.existsByToken(jwt)) {
                logger.warn("Invalid JWT token detected.");
                writeUnauthorizedResponse(response, "Token is blacklisted/revoked");
                return;
            }

            final String userEmail = jwtService.extractUsername(jwt);

            // Only proceed if we have a username and the SecurityContext is not yet authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,                          // credentials (null after auth)
                                    userDetails.getAuthorities()
                                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication into the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("JWT authenticated user: {}", userEmail);
                }
            }

        } catch (ExpiredJwtException ex) {
            logger.warn("Expired JWT token.");
            writeUnauthorizedResponse(response, "JWT token has expired");
            return;
        } catch (Exception ex) {
            // Log and return a clean 401 JSON — never let exceptions bubble up from filters
            logger.warn("Invalid JWT token detected.");
            writeUnauthorizedResponse(response, "Invalid or expired JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /** Writes a standard {@link ApiResponse} error JSON with HTTP 401 status. */
    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writeValue(response.getOutputStream(), ApiResponse.error(message));
    }
}
