package com.PrepTrack_AI.Fullstack_Project.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing a Resume Analysis (ATS parsing, score, suggestions).
 */
@Entity
@Table(
        name = "resume_analyses",
        indexes = {
                @Index(name = "idx_resume_analyses_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false, foreignKey = @ForeignKey(name = "fk_resume_analyses_resume"))
    private Resume resume;

    @Column(name = "ats_score", nullable = false)
    private Integer atsScore;

    @Column(name = "skills", nullable = false, columnDefinition = "TEXT")
    private String skills;

    @Column(name = "missing_keywords", nullable = false, columnDefinition = "TEXT")
    private String missingKeywords;

    @Column(name = "suggestions", nullable = false, columnDefinition = "TEXT")
    private String suggestions;
}
