package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.PermissionRequest;
import com.PrepTrack_AI.Fullstack_Project.dto.PermissionResponse;
import com.PrepTrack_AI.Fullstack_Project.entity.Permission;
import com.PrepTrack_AI.Fullstack_Project.exception.DuplicateResourceException;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.repository.PermissionRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testPermission = Permission.builder()
                .id(1L)
                .name("INTERVIEW_CREATE")
                .build();
    }

    @Test
    void getAllPermissions_Success() {
        org.springframework.data.domain.Page<Permission> page = new org.springframework.data.domain.PageImpl<>(List.of(testPermission));
        when(permissionRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        ApiResponse<com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse<PermissionResponse>> response = permissionService.getAllPermissions(0, 10);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().getContent().size());
        assertEquals("INTERVIEW_CREATE", response.getData().getContent().get(0).getName());
    }

    @Test
    void createPermission_Success() {
        PermissionRequest request = PermissionRequest.builder()
                .name("NEW_PERMISSION")
                .build();

        when(permissionRepository.existsByName("NEW_PERMISSION")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> {
            Permission permission = invocation.getArgument(0);
            permission.setId(2L);
            return permission;
        });

        ApiResponse<PermissionResponse> response = permissionService.createPermission(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("NEW_PERMISSION", response.getData().getName());
        assertEquals(2L, response.getData().getId());
    }

    @Test
    void createPermission_DuplicateName_ThrowsException() {
        PermissionRequest request = PermissionRequest.builder()
                .name("INTERVIEW_CREATE")
                .build();

        when(permissionRepository.existsByName("INTERVIEW_CREATE")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            permissionService.createPermission(request);
        });
    }

    @Test
    void deletePermission_Success() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(testPermission));
        when(roleRepository.existsByPermissionsId(1L)).thenReturn(false);
        doNothing().when(permissionRepository).delete(testPermission);

        ApiResponse<Void> response = permissionService.deletePermission(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        verify(permissionRepository, times(1)).delete(testPermission);
    }

    @Test
    void deletePermission_NotFound_ThrowsException() {
        when(permissionRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            permissionService.deletePermission(2L);
        });
    }

    @Test
    void deletePermission_MappedToRoles_ThrowsException() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(testPermission));
        when(roleRepository.existsByPermissionsId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            permissionService.deletePermission(1L);
        });
    }
}
