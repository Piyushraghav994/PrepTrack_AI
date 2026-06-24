package com.PrepTrack_AI.Fullstack_Project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * JPA entity representing user authorization permissions.
 *
 * <p>Inherits audit tracking properties from {@link BaseEntity}.</p>
 */
@Entity
@Table(
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_permissions_name", columnNames = "name")
        },
        indexes = {
                @Index(name = "idx_permissions_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Permission name is required")
    private String name;
}
