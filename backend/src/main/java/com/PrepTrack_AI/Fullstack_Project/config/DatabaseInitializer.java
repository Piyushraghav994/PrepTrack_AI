package com.PrepTrack_AI.Fullstack_Project.config;

import com.PrepTrack_AI.Fullstack_Project.entity.*;
import com.PrepTrack_AI.Fullstack_Project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

/**
 * Initializes database with default Roles, Permissions, a default Admin user,
 * and sample Interviews and Interview Questions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

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

        // 4. Seed Default Interviews & Questions
        if (interviewRepository.count() == 0) {
            log.info("No interviews found. Seeding sample interviews and questions...");

            // Seeding Java Backend Interview
            Interview javaInterview = Interview.builder()
                    .title("Java Backend Engineer Mock Interview")
                    .company("Google")
                    .role("Software Engineer")
                    .difficulty(Difficulty.MEDIUM)
                    .description("Comprehensive assessment of Java Core, Spring Boot, Concurrency, and System Design concepts.")
                    .build();
            javaInterview = interviewRepository.save(javaInterview);

            InterviewQuestion q1 = InterviewQuestion.builder()
                    .question("Explain the difference between Optimistic and Pessimistic locking.")
                    .answer("Optimistic locking assumes multiple transactions can complete without affecting each other. It uses a version field. Pessimistic locking locks records so no other transaction can read/write until it's released.")
                    .topic("Database")
                    .category("Systems")
                    .interview(javaInterview)
                    .build();

            InterviewQuestion q2 = InterviewQuestion.builder()
                    .question("What is the difference between a HashMap and ConcurrentHashMap in Java?")
                    .answer("HashMap is not thread-safe. ConcurrentHashMap is thread-safe and utilizes bucket-level locking or CAS operations to allow highly concurrent reads and writes without locking the entire table.")
                    .topic("Java")
                    .category("Core")
                    .interview(javaInterview)
                    .build();

            InterviewQuestion q3 = InterviewQuestion.builder()
                    .question("Explain Spring Boot starter dependency mechanism.")
                    .answer("Starters are a set of convenient dependency descriptors that you can include in your application. They configure all the necessary jars and default auto-configurations under the hood.")
                    .topic("Spring Boot")
                    .category("Frameworks")
                    .interview(javaInterview)
                    .build();

            interviewQuestionRepository.saveAll(List.of(q1, q2, q3));

            // Seeding Frontend React Interview
            Interview reactInterview = Interview.builder()
                    .title("Frontend React Developer Mock Interview")
                    .company("Meta")
                    .role("Frontend Developer")
                    .difficulty(Difficulty.EASY)
                    .description("Basic assessment of DOM manipulation, component lifecycle, state management, and basic React hooks.")
                    .build();
            reactInterview = interviewRepository.save(reactInterview);

            InterviewQuestion q4 = InterviewQuestion.builder()
                    .question("What are React Hooks and why were they introduced?")
                    .answer("Hooks allow you to use state and other React features in functional components without writing a class. They solve problems of code reuse, complex components, and confusing classes.")
                    .topic("React")
                    .category("Core")
                    .interview(reactInterview)
                    .build();

            InterviewQuestion q5 = InterviewQuestion.builder()
                    .question("Explain the virtual DOM.")
                    .answer("The virtual DOM is a programming concept where a virtual representation of the UI is kept in memory and synced with the real DOM by a library such as ReactDOM.")
                    .topic("React")
                    .category("Core")
                    .interview(reactInterview)
                    .build();

            interviewQuestionRepository.saveAll(List.of(q4, q5));
            log.info("Seeded 2 mock interviews and 5 questions.");
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
