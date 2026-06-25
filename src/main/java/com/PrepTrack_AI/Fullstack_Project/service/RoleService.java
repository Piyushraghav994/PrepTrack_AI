package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.RoleRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.RoleResponse;

import java.util.List;

/**
 * Service interface for Role management operations.
 */
public interface RoleService {

    /**
     * Retrieves all roles in the system.
     */
    ApiResponse<List<RoleResponse>> getAllRoles();

    /**
     * Retrieves a role by its database ID.
     */
    ApiResponse<RoleResponse> getRoleById(Long id);

    /**
     * Creates a new role with the specified permissions.
     */
    ApiResponse<RoleResponse> createRole(RoleRequest request);

    /**
     * Updates an existing role (both its name and/or associated permissions).
     */
    ApiResponse<RoleResponse> updateRole(Long id, RoleRequest request);

    /**
     * Deletes a role by ID. Throws exceptions if the role is system default or in use.
     */
    ApiResponse<Void> deleteRole(Long id);
}
