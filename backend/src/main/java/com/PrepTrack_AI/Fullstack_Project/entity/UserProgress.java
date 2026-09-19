package com.PrepTrack_AI.Fullstack_Project.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing user progress, streak, and scores.
 */
@Entity
@Table(
        name = "user_progress",
        indexes = {
                @Index(name = "idx_user_progress_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_user_progress_user"))
    private User user;

    @Column(name = "completed_questions", nullable = false)
    @Builder.Default
    private Integer completedQuestions = 0;

    @Column(name = "completed_interviews", nullable = false)
    @Builder.Default
    private Integer completedInterviews = 0;

    @Column(name = "current_streak", nullable = false)
    @Builder.Default
    private Integer currentStreak = 0;

    @Column(name = "total_score", nullable = false)
    @Builder.Default
    private Integer totalScore = 0;
}
