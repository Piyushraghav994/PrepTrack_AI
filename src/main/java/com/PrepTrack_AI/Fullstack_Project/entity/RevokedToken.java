package com.PrepTrack_AI.Fullstack_Project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing blacklisted access tokens (JWTs) for logout enforcement.
 *
 * <p>Inherits audit tracking properties from {@link BaseEntity}.</p>
 */
@Entity
@Table(
        name = "revoked_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_revoked_tokens_token", columnNames = "token")
        },
        indexes = {
                @Index(name = "idx_revoked_tokens_token", columnList = "token"),
                @Index(name = "idx_revoked_tokens_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    @NotBlank(message = "Token is required")
    private String token;

    @Column(name = "expiry_date", nullable = false)
    @NotNull(message = "Expiry date is required")
    private LocalDateTime expiryDate;
}
