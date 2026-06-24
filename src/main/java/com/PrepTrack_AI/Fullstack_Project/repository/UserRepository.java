package com.PrepTrack_AI.Fullstack_Project.repository;

import com.PrepTrack_AI.Fullstack_Project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address (the login identifier).
     */
    Optional<User> findByEmail(String email);

    /**
     * Returns {@code true} if any user exists with the given email.
     * Used during registration to prevent duplicates.
     */
    boolean existsByEmail(String email);
}
