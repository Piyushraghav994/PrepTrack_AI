package com.PrepTrack_AI.Fullstack_Project.audit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_logs_performed_by", columnList = "performed_by"),
                @Index(name = "idx_audit_logs_timestamp", columnList = "timestamp"),
                @Index(name = "idx_audit_logs_entity_name", columnList = "entity_name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action", nullable = false, length = 100)
    @NotBlank(message = "Action is required")
    private String action;

    @Column(name = "performed_by", nullable = false, length = 150)
    @NotBlank(message = "Performed by user is required")
    private String performedBy;

    @Column(name = "timestamp", nullable = false)
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @Column(name = "entity_name", length = 100)
    private String entityName;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "status", nullable = false, length = 50)
    @NotBlank(message = "Status is required")
    private String status;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
