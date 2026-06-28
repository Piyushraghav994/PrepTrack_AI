package com.PrepTrack_AI.Fullstack_Project.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponseDTO {
    private Long id;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;
    private String entityName;
    private Long entityId;
    private String description;
    private String ipAddress;
    private String status;
}
