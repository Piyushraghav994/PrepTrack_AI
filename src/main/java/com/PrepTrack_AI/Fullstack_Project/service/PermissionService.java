package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.PermissionRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.PermissionResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;

import java.util.List;

/**
 * Service interface for Permission management operations.
 */
public interface PermissionService {

    /**
     * Retrieves all permissions in the system.
     */
    ApiResponse<PagedResponse<PermissionResponse>> getAllPermissions(int page, int size);

    /**
     * Creates a new permission.
     */
    ApiResponse<PermissionResponse> createPermission(PermissionRequest request);

    /**
     * Deletes a permission by ID. Prevents deletion if the permission is mapped to any role.
     */
    ApiResponse<Void> deletePermission(Long id);
}
