package com.PrepTrack_AI.Fullstack_Project.audit.mapper;

import com.PrepTrack_AI.Fullstack_Project.audit.dto.AuditLogResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.audit.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditLogMapper {

    AuditLogResponseDTO toDto(AuditLog auditLog);

    List<AuditLogResponseDTO> toDtoList(List<AuditLog> logs);
}
