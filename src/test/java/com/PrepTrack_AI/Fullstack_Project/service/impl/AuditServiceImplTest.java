package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.audit.dto.AuditLogResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.audit.entity.AuditLog;
import com.PrepTrack_AI.Fullstack_Project.audit.mapper.AuditLogMapper;
import com.PrepTrack_AI.Fullstack_Project.audit.repository.AuditLogRepository;
import com.PrepTrack_AI.Fullstack_Project.audit.service.impl.AuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditServiceImpl auditService;

    private AuditLog auditLog;
    private AuditLogResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        auditLog = AuditLog.builder()
                .id(1L)
                .action("CREATE_USER")
                .performedBy("john.doe@example.com")
                .timestamp(LocalDateTime.now())
                .entityName("User")
                .entityId(100L)
                .description("Created new user")
                .ipAddress("127.0.0.1")
                .status("SUCCESS")
                .build();

        responseDTO = AuditLogResponseDTO.builder()
                .id(1L)
                .action("CREATE_USER")
                .performedBy("john.doe@example.com")
                .timestamp(auditLog.getTimestamp())
                .entityName("User")
                .entityId(100L)
                .description("Created new user")
                .ipAddress("127.0.0.1")
                .status("SUCCESS")
                .build();
    }

    @Test
    void logAction_Success() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        auditService.logAction(
                "CREATE_USER",
                "john.doe@example.com",
                "User",
                100L,
                "Created new user",
                "127.0.0.1",
                "SUCCESS"
        );

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void getAllLogs_Success() {
        when(auditLogRepository.findAll()).thenReturn(Collections.singletonList(auditLog));
        when(auditLogMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(responseDTO));

        List<AuditLogResponseDTO> logs = auditService.getAllLogs();

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("CREATE_USER", logs.get(0).getAction());
        verify(auditLogRepository, times(1)).findAll();
    }

    @Test
    void getLogsByUser_Success() {
        when(auditLogRepository.findByPerformedBy("john.doe@example.com")).thenReturn(Collections.singletonList(auditLog));
        when(auditLogMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(responseDTO));

        List<AuditLogResponseDTO> logs = auditService.getLogsByUser("john.doe@example.com");

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("john.doe@example.com", logs.get(0).getPerformedBy());
        verify(auditLogRepository, times(1)).findByPerformedBy("john.doe@example.com");
    }

    @Test
    void getLogsByEntity_Success() {
        when(auditLogRepository.findByEntityName("User")).thenReturn(Collections.singletonList(auditLog));
        when(auditLogMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(responseDTO));

        List<AuditLogResponseDTO> logs = auditService.getLogsByEntity("User");

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("User", logs.get(0).getEntityName());
        verify(auditLogRepository, times(1)).findByEntityName("User");
    }

    @Test
    void getLogsByAction_Success() {
        when(auditLogRepository.findByAction("CREATE_USER")).thenReturn(Collections.singletonList(auditLog));
        when(auditLogMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(responseDTO));

        List<AuditLogResponseDTO> logs = auditService.getLogsByAction("CREATE_USER");

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("CREATE_USER", logs.get(0).getAction());
        verify(auditLogRepository, times(1)).findByAction("CREATE_USER");
    }
}
