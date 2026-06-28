package com.PrepTrack_AI.Fullstack_Project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing an Interview.
 */
@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    @NotBlank(message = "Title is required")
    private String title;

    @Column(name = "company", nullable = false, length = 100)
    @NotBlank(message = "Company is required")
    private String company;

    @Column(name = "role", nullable = false, length = 100)
    @NotBlank(message = "Role is required")
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewQuestion> questions = new ArrayList<>();
}
