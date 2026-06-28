package com.PrepTrack_AI.Fullstack_Project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing a Resume uploaded by a user.
 */
@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_url", nullable = false, length = 500)
    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ResumeAnalysis analysis;
}
