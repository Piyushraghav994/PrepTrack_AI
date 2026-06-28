package com.PrepTrack_AI.Fullstack_Project.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing Interview Feedback.
 */
@Entity
@Table(name = "interview_feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewFeedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession session;

    @Column(name = "strengths", nullable = false, columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "weaknesses", nullable = false, columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "recommendations", nullable = false, columnDefinition = "TEXT")
    private String recommendations;
}
