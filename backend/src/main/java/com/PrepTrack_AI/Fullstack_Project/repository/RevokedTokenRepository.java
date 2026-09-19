package com.PrepTrack_AI.Fullstack_Project.repository;

import com.PrepTrack_AI.Fullstack_Project.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Spring Data JPA repository for {@link RevokedToken} entities.
 */
@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    /**
     * Checks if a specific JWT access token has been blacklisted.
     */
    boolean existsByToken(String token);

    /**
     * Deletes all blacklisted tokens whose expiry date has passed.
     * Used for database cleanup tasks.
     */
    void deleteByExpiryDateBefore(LocalDateTime expiryDate);
}
