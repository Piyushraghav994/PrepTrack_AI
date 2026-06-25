package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.PermissionRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.PermissionResponse;
import com.PrepTrack_AI.Fullstack_Project.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Permission management operations.
 * Protected under ADMIN access only.
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Permission Management", description = "Endpoints for managing system permissions. Admin only.")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(
            summary = "Get all permissions",
            description = "Retrieves a list of all permissions defined in the system."
    )
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @PostMapping
    @Operation(
            summary = "Create permission",
            description = "Creates a new system permission."
    )
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@Valid @RequestBody PermissionRequest request) {
        ApiResponse<PermissionResponse> response = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete permission",
            description = "Deletes a permission by ID. Fails if the permission is currently mapped to any roles."
    )
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.deletePermission(id));
    }
}
