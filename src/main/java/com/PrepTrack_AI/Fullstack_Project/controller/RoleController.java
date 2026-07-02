package com.PrepTrack_AI.Fullstack_Project.controller;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.RoleRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.RoleResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;
import com.PrepTrack_AI.Fullstack_Project.service.RoleService;
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
 * REST controller for Role management operations.
 * Protected under ADMIN access only.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Role Management", description = "Endpoints for managing user roles and permission mappings. Admin only.")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(
            summary = "Get all roles",
            description = "Retrieves a list of all roles in the system, along with their mapped permissions."
    )
    public ResponseEntity<ApiResponse<PagedResponse<RoleResponse>>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(roleService.getAllRoles(page, size));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get role by ID",
            description = "Retrieves detail of a specific role by database ID, including its permissions."
    )
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create role",
            description = "Creates a new role and associates it with the specified permission names."
    )
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        ApiResponse<RoleResponse> response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update role",
            description = "Updates an existing role's name and its mapped permissions."
    )
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete role",
            description = "Deletes a role by ID. Fails if the role is a system default or is currently assigned to users."
    )
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }
}
