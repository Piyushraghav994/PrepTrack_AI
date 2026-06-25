package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.RoleRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.RoleResponse;
import com.PrepTrack_AI.Fullstack_Project.entity.Permission;
import com.PrepTrack_AI.Fullstack_Project.entity.Role;
import com.PrepTrack_AI.Fullstack_Project.exception.DuplicateResourceException;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.repository.PermissionRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.RoleRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for managing Roles and their mappings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    private static final Set<String> SYSTEM_DEFAULT_ROLES = Set.of("ROLE_STUDENT", "ROLE_ADMIN");

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        log.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        List<RoleResponse> responses = roles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Roles fetched successfully", responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<RoleResponse> getRoleById(Long id) {
        log.info("Fetching role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return ApiResponse.success("Role fetched successfully", mapToResponse(role));
    }

    @Override
    public ApiResponse<RoleResponse> createRole(RoleRequest request) {
        log.info("Creating role with name: {}", request.getName());

        if (roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role", "name", request.getName());
        }

        Set<Permission> permissions = resolvePermissions(request.getPermissionNames());

        Role role = Role.builder()
                .name(request.getName())
                .permissions(permissions)
                .build();

        role.setCreatedBy("SYSTEM"); // Spring security context auditor will overwrite if authenticated
        role.setUpdatedBy("SYSTEM");

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully: {}", savedRole.getName());
        return ApiResponse.success("Role created successfully", mapToResponse(savedRole));
    }

    @Override
    public ApiResponse<RoleResponse> updateRole(Long id, RoleRequest request) {
        log.info("Updating role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // Prevent renaming system default roles
        if (SYSTEM_DEFAULT_ROLES.contains(role.getName()) && !role.getName().equals(request.getName())) {
            throw new IllegalArgumentException("Cannot rename system default roles: " + role.getName());
        }

        // Check duplicate name if renamed
        if (!role.getName().equalsIgnoreCase(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role", "name", request.getName());
        }

        role.setName(request.getName());
        Set<Permission> permissions = resolvePermissions(request.getPermissionNames());
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated successfully: {}", updatedRole.getName());
        return ApiResponse.success("Role updated successfully", mapToResponse(updatedRole));
    }

    @Override
    public ApiResponse<Void> deleteRole(Long id) {
        log.info("Deleting role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // Check default roles
        if (SYSTEM_DEFAULT_ROLES.contains(role.getName())) {
            throw new IllegalArgumentException("Cannot delete system default roles: " + role.getName());
        }

        // Check if assigned to any user
        if (userRepository.existsByRoleId(id)) {
            throw new IllegalStateException("Cannot delete role as it is currently assigned to active users");
        }

        roleRepository.delete(role);
        log.info("Role deleted successfully: {}", role.getName());
        return ApiResponse.success("Role deleted successfully");
    }

    private Set<Permission> resolvePermissions(Set<String> permissionNames) {
        if (permissionNames == null || permissionNames.isEmpty()) {
            return new HashSet<>();
        }
        Set<Permission> permissions = new HashSet<>();
        for (String name : permissionNames) {
            Permission permission = permissionRepository.findByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission", "name", name));
            permissions.add(permission);
        }
        return permissions;
    }

    private RoleResponse mapToResponse(Role role) {
        Set<String> permissionNames = role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(permissionNames)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
