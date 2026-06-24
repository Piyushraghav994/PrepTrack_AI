package com.PrepTrack_AI.Fullstack_Project.config;

import com.PrepTrack_AI.Fullstack_Project.security.JwtAuthEntryPoint;
import com.PrepTrack_AI.Fullstack_Project.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central Spring Security configuration for PrepTrack.
 *
 * <p>Security strategy:</p>
 * <ul>
 *   <li>Stateless sessions (JWT only — no HTTP session)</li>
 *   <li>CSRF disabled (safe for stateless REST APIs)</li>
 *   <li>Public routes: {@code /api/auth/**} and Swagger UI</li>
 *   <li>All other routes require a valid JWT Bearer token</li>
 *   <li>{@link JwtAuthFilter} runs before Spring's built-in username/password filter</li>
 *   <li>Custom {@link JwtAuthEntryPoint} returns clean JSON on 401 instead of HTML</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity                    // enables @PreAuthorize / @PostAuthorize on controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final JwtAuthEntryPoint authEntryPoint;

    // ── Endpoints open to everyone (no token required) ────────────────────────
    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**"
    };

    // ── Security Filter Chain ─────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless JWT APIs
                .csrf(AbstractHttpConfigurer::disable)

                // CORS — permissive for development; tighten in production
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Return JSON 401 instead of redirect/HTML on unauthorized access
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))

                // Route-level authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )

                // No HTTP sessions — purely stateless (JWT handles state)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Wire in our DaoAuthenticationProvider
                .authenticationProvider(authenticationProvider())

                // Insert JWT filter before Spring's built-in username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── Authentication Provider ───────────────────────────────────────────────

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Spring Security 7 (Spring Boot 4.x): constructor requires UserDetailsService directly
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── Password Encoder ──────────────────────────────────────────────────────

    /**
     * BCrypt with default strength (10 rounds).
     * Declared here to avoid circular dependency:
     * SecurityConfig → AuthServiceImpl → PasswordEncoder → SecurityConfig.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── CORS Configuration ────────────────────────────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));           // restrict to your frontend URL in production
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
