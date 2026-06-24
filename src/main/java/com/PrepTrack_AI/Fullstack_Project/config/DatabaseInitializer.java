package com.PrepTrack_AI.Fullstack_Project.config;

import com.PrepTrack_AI.Fullstack_Project.entity.Permission;
import com.PrepTrack_AI.Fullstack_Project.entity.Role;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.repository.PermissionRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.RoleRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Initializes database with default Roles, Permissions, and a default Admin user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting database seeding initializer...");

        // 1. Seed Permissions
        Permission userRead = getOrCreatePermission("USER_READ");
        Permission userWrite = getOrCreatePermission("USER_WRITE");
        Permission interviewCreate = getOrCreatePermission("INTERVIEW_CREATE");
        Permission interviewDelete = getOrCreatePermission("INTERVIEW_DELETE");
        Permission adminAccess = getOrCreatePermission("ADMIN_ACCESS");

        // 2. Seed Roles
        // ROLE_STUDENT
        Role studentRole = roleRepository.findByName("ROLE_STUDENT").orElse(null);
        if (studentRole == null) {
            studentRole = Role.builder()
                    .name("ROLE_STUDENT")
                    .permissions(new HashSet<>(List.of(userRead, userWrite, interviewCreate)))
                    .build();
            studentRole = roleRepository.save(studentRole);
            log.info("Seeded default role: ROLE_STUDENT");
        }

        // ROLE_ADMIN
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
        if (adminRole == null) {
            adminRole = Role.builder()
                    .name("ROLE_ADMIN")
                    .permissions(new HashSet<>(List.of(userRead, userWrite, interviewCreate, interviewDelete, adminAccess)))
                    .build();
            adminRole = roleRepository.save(adminRole);
            log.info("Seeded default role: ROLE_ADMIN");
        }

        // 3. Seed Default Admin User
        String adminEmail = "admin@preptrack.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .fullName("Platform Administrator")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("AdminPassword123!"))
                    .college("PrepTrack HQ")
                    .branch("Administration")
                    .passoutYear(2026)
                    .role(adminRole)
                    .emailVerified(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .build();
            admin.setCreatedBy("SYSTEM");
            admin.setUpdatedBy("SYSTEM");
            userRepository.save(admin);
            log.info("Seeded default admin user: {}", adminEmail);
        }

        log.info("Database seeding completed successfully.");
    }

    private Permission getOrCreatePermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = Permission.builder()
                            .name(name)
                            .build();
                    Permission saved = permissionRepository.save(permission);
                    log.info("Seeded permission: {}", name);
                    return saved;
                });
    }
}
