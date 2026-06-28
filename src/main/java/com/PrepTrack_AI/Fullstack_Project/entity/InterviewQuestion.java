package com.PrepTrack_AI.Fullstack_Project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * JPA entity representing an Interview Question.
 */
@Entity
@Table(name = "interview_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Question text is required")
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Answer text is required")
    private String answer;

    @Column(name = "topic", nullable = false, length = 100)
    @NotBlank(message = "Topic is required")
    private String topic;

    @Column(name = "category", nullable = false, length = 100)
    @NotBlank(message = "Category is required")
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    @JsonIgnore
    private Interview interview;
}
