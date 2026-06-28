package com.PrepTrack_AI.Fullstack_Project.audit.service.impl;

import com.PrepTrack_AI.Fullstack_Project.audit.dto.AuditLogResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.audit.entity.AuditLog;
import com.PrepTrack_AI.Fullstack_Project.audit.mapper.AuditLogMapper;
import com.PrepTrack_AI.Fullstack_Project.audit.repository.AuditLogRepository;
import com.PrepTrack_AI.Fullstack_Project.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public void logAction(
            String action,
            String performedBy,
            String entityName,
            Long entityId,
            String description,
            String ipAddress,
            String status
    ) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .entityName(entityName)
                .entityId(entityId)
                .description(description)
                .ipAddress(ipAddress)
                .status(status)
                .build();

        auditLogRepository.save(auditLog);

        // Also write into application logs (Logback integration)
        if (entityId != null) {
            logger.info("User [{}] performed {} on {} entity with id={} ({})", performedBy, action, entityName, entityId, status);
        } else {
            logger.info("User [{}] performed {} on {} entity ({})", performedBy, action, entityName, status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getAllLogs() {
        List<AuditLog> logs = auditLogRepository.findAll();
        return auditLogMapper.toDtoList(logs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getLogsByUser(String email) {
        List<AuditLog> logs = auditLogRepository.findByPerformedBy(email);
        return auditLogMapper.toDtoList(logs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getLogsByEntity(String entityName) {
        List<AuditLog> logs = auditLogRepository.findByEntityName(entityName);
        return auditLogMapper.toDtoList(logs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getLogsByAction(String action) {
        List<AuditLog> logs = auditLogRepository.findByAction(action);
        return auditLogMapper.toDtoList(logs);
    }
}
