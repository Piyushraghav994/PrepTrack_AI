package com.PrepTrack_AI.Fullstack_Project.security;

import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom {@link UserDetailsService} implementation that loads a {@link com.PrepTrack_AI.Fullstack_Project.entity.User}
 * from the database by email address.
 *
 * <p>Kept in the {@code security} package to avoid circular dependency:
 * SecurityConfig → this service → UserRepository (no back-reference to SecurityConfig).</p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their email address (used as the Spring Security username).
     *
     * @param email the email address to look up
     * @return the matching {@link UserDetails} (our {@code User} entity)
     * @throws UsernameNotFoundException if no user exists with this email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with email: " + email));
    }
}
