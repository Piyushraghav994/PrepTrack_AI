package com.PrepTrack_AI.Fullstack_Project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for all JWT operations using JJWT 0.12.x API.
 *
 * <ul>
 *   <li>Token generation with HMAC-SHA256 signing</li>
 *   <li>Claim extraction (username, expiry, custom claims)</li>
 *   <li>Token validation against a given {@link UserDetails}</li>
 * </ul>
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // ── Token Generation ──────────────────────────────────────────────────────

    /**
     * Generates a JWT token for the given user with default (empty) extra claims.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional custom claims embedded in the payload.
     *
     * @param extraClaims additional key-value pairs to embed (e.g. role, userId)
     * @param userDetails Spring Security user details — subject = username (email)
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())          // email used as subject
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())                   // HMAC-SHA256
                .compact();
    }

    // ── Claim Extraction ──────────────────────────────────────────────────────

    /**
     * Extracts the username (email) from the token's subject claim.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a single claim from the token using the provided resolver function.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the token's subject matches the user's username
     * and the token has not yet expired.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Decodes the Base64-encoded secret from application.properties and derives
     * the HMAC-SHA256 signing key. The secret must be at least 256 bits (32 bytes).
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
