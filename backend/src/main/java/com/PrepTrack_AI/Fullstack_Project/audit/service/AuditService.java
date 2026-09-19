package com.PrepTrack_AI.Fullstack_Project.audit.service;

import com.PrepTrack_AI.Fullstack_Project.audit.dto.AuditLogResponseDTO;

import java.util.List;

public interface AuditService {

    void logAction(
            String action,
            String performedBy,
            String entityName,
            Long entityId,
            String description,
            String ipAddress,
            String status
    );

    List<AuditLogResponseDTO> getAllLogs();

    List<AuditLogResponseDTO> getLogsByUser(String email);

    List<AuditLogResponseDTO> getLogsByEntity(String entityName);

    List<AuditLogResponseDTO> getLogsByAction(String action);
}
