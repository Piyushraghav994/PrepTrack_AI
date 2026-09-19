package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.PermissionRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.PermissionResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.PrepTrack_AI.Fullstack_Project.entity.Permission;
import com.PrepTrack_AI.Fullstack_Project.exception.DuplicateResourceException;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.repository.PermissionRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.RoleRepository;
import com.PrepTrack_AI.Fullstack_Project.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing Permissions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<PermissionResponse>> getAllPermissions(int page, int size) {
        log.info("Fetching all permissions, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        List<PermissionResponse> content = permissionPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        PagedResponse<PermissionResponse> response = PagedResponse.<PermissionResponse>builder()
                .content(content)
                .pageNumber(permissionPage.getNumber())
                .pageSize(permissionPage.getSize())
                .totalElements(permissionPage.getTotalElements())
                .totalPages(permissionPage.getTotalPages())
                .last(permissionPage.isLast())
                .build();

        return ApiResponse.success("Permissions fetched successfully", response);
    }

    @Override
    public ApiResponse<PermissionResponse> createPermission(PermissionRequest request) {
        log.info("Creating permission with name: {}", request.getName());

        if (permissionRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Permission", "name", request.getName());
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .build();

        permission.setCreatedBy("SYSTEM"); // Spring security context auditor will overwrite if authenticated
        permission.setUpdatedBy("SYSTEM");

        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created successfully: {}", savedPermission.getName());
        return ApiResponse.success("Permission created successfully", mapToResponse(savedPermission));
    }

    @Override
    public ApiResponse<Void> deletePermission(Long id) {
        log.info("Deleting permission with ID: {}", id);
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));

        // Check if permission is assigned to any role
        if (roleRepository.existsByPermissionsId(id)) {
            throw new IllegalStateException("Cannot delete permission as it is currently assigned to active roles");
        }

        permissionRepository.delete(permission);
        log.info("Permission deleted successfully: {}", permission.getName());
        return ApiResponse.success("Permission deleted successfully");
    }

    private PermissionResponse mapToResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}
