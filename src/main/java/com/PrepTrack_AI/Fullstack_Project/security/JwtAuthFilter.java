package com.PrepTrack_AI.Fullstack_Project.security;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.PrepTrack_AI.Fullstack_Project.repository.RevokedTokenRepository;
import java.io.IOException;

/**
 * JWT authentication filter that intercepts every HTTP request exactly once.
 *
 * <p>Processing flow:</p>
 * <ol>
 *   <li>Read the {@code Authorization} header.</li>
 *   <li>If missing or not prefixed with {@code "Bearer "}, skip to next filter.</li>
 *   <li>Extract the JWT token and parse the username (email).</li>
 *   <li>Load {@link UserDetails} from the database.</li>
 *   <li>Validate the token — if valid, populate the {@link org.springframework.security.core.context.SecurityContext}.</li>
 *   <li>On any JWT error, write a clean 401 JSON response instead of a stack trace.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
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
                log.warn("JWT validation failed: Token is blacklisted/revoked");
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
                    log.debug("JWT authenticated user: {}", userEmail);
                }
            }

        } catch (Exception ex) {
            // Log and return a clean 401 JSON — never let exceptions bubble up from filters
            log.warn("JWT validation failed for request [{}]: {}", request.getRequestURI(), ex.getMessage());
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
