package com.PrepTrack_AI.Fullstack_Project.audit.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.audit.dto.AuditLogResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.audit.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit Management", description = "Endpoints for administrator auditing of system operations. Admin only.")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/all")
    @Operation(summary = "Get all audit logs", description = "Retrieves all audit logs in the system.")
    public ResponseEntity<ApiResponse<List<AuditLogResponseDTO>>> getAllLogs() {
        return ResponseEntity.ok(ApiResponse.success("Audit logs fetched successfully", auditService.getAllLogs()));
    }

    @GetMapping("/user/{email}")
    @Operation(summary = "Get audit logs by user", description = "Retrieves all audit logs executed by a specific user email.")
    public ResponseEntity<ApiResponse<List<AuditLogResponseDTO>>> getLogsByUser(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.success("Audit logs for user fetched successfully", auditService.getLogsByUser(email)));
    }

    @GetMapping("/entity/{entityName}")
    @Operation(summary = "Get audit logs by entity type", description = "Retrieves all audit logs relating to a specific entity type.")
    public ResponseEntity<ApiResponse<List<AuditLogResponseDTO>>> getLogsByEntity(@PathVariable String entityName) {
        return ResponseEntity.ok(ApiResponse.success("Audit logs for entity fetched successfully", auditService.getLogsByEntity(entityName)));
    }

    @GetMapping("/action/{action}")
    @Operation(summary = "Get audit logs by action type", description = "Retrieves all audit logs mapped to a specific action type.")
    public ResponseEntity<ApiResponse<List<AuditLogResponseDTO>>> getLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(ApiResponse.success("Audit logs for action fetched successfully", auditService.getLogsByAction(action)));
    }
}
