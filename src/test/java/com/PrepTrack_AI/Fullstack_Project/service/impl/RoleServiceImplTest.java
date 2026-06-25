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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testPermission = Permission.builder()
                .id(1L)
                .name("USER_READ")
                .build();

        testRole = Role.builder()
                .id(1L)
                .name("ROLE_CUSTOM")
                .permissions(new HashSet<>(List.of(testPermission)))
                .build();
    }

    @Test
    void getAllRoles_Success() {
        when(roleRepository.findAll()).thenReturn(List.of(testRole));

        ApiResponse<List<RoleResponse>> response = roleService.getAllRoles();

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals("ROLE_CUSTOM", response.getData().get(0).getName());
        assertTrue(response.getData().get(0).getPermissions().contains("USER_READ"));
    }

    @Test
    void getRoleById_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        ApiResponse<RoleResponse> response = roleService.getRoleById(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("ROLE_CUSTOM", response.getData().getName());
    }

    @Test
    void getRoleById_NotFound_ThrowsException() {
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            roleService.getRoleById(2L);
        });
    }

    @Test
    void createRole_Success() {
        RoleRequest request = RoleRequest.builder()
                .name("ROLE_NEW")
                .permissionNames(Set.of("USER_READ"))
                .build();

        when(roleRepository.existsByName("ROLE_NEW")).thenReturn(false);
        when(permissionRepository.findByName("USER_READ")).thenReturn(Optional.of(testPermission));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(2L);
            return role;
        });

        ApiResponse<RoleResponse> response = roleService.createRole(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("ROLE_NEW", response.getData().getName());
        assertEquals(2L, response.getData().getId());
        assertTrue(response.getData().getPermissions().contains("USER_READ"));
    }

    @Test
    void createRole_DuplicateName_ThrowsException() {
        RoleRequest request = RoleRequest.builder()
                .name("ROLE_CUSTOM")
                .build();

        when(roleRepository.existsByName("ROLE_CUSTOM")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            roleService.createRole(request);
        });
    }

    @Test
    void updateRole_Success() {
        RoleRequest request = RoleRequest.builder()
                .name("ROLE_CUSTOM_UPDATED")
                .permissionNames(Set.of("USER_READ"))
                .build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleRepository.existsByName("ROLE_CUSTOM_UPDATED")).thenReturn(false);
        when(permissionRepository.findByName("USER_READ")).thenReturn(Optional.of(testPermission));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<RoleResponse> response = roleService.updateRole(1L, request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("ROLE_CUSTOM_UPDATED", response.getData().getName());
    }

    @Test
    void updateRole_RenameDefaultRole_ThrowsException() {
        Role defaultRole = Role.builder().id(10L).name("ROLE_ADMIN").build();
        RoleRequest request = RoleRequest.builder().name("ROLE_SUPER_ADMIN").build();

        when(roleRepository.findById(10L)).thenReturn(Optional.of(defaultRole));

        assertThrows(IllegalArgumentException.class, () -> {
            roleService.updateRole(10L, request);
        });
    }

    @Test
    void deleteRole_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userRepository.existsByRoleId(1L)).thenReturn(false);
        doNothing().when(roleRepository).delete(testRole);

        ApiResponse<Void> response = roleService.deleteRole(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        verify(roleRepository, times(1)).delete(testRole);
    }

    @Test
    void deleteRole_SystemDefault_ThrowsException() {
        Role defaultRole = Role.builder().id(10L).name("ROLE_ADMIN").build();
        when(roleRepository.findById(10L)).thenReturn(Optional.of(defaultRole));

        assertThrows(IllegalArgumentException.class, () -> {
            roleService.deleteRole(10L);
        });
    }

    @Test
    void deleteRole_AssignedToUsers_ThrowsException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userRepository.existsByRoleId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            roleService.deleteRole(1L);
        });
    }
}
