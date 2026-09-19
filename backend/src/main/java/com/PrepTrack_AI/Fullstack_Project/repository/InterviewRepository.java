package com.PrepTrack_AI.Fullstack_Project.repository;

import com.PrepTrack_AI.Fullstack_Project.entity.Difficulty;
import com.PrepTrack_AI.Fullstack_Project.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Interview} entities.
 */
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByDifficulty(Difficulty difficulty);
    List<Interview> findByRoleContainingIgnoreCase(String role);
    Page<Interview> findByDifficulty(Difficulty difficulty, Pageable pageable);
    Page<Interview> findByRoleContainingIgnoreCase(String role, Pageable pageable);
    Page<Interview> findByDifficultyAndRoleContainingIgnoreCase(Difficulty difficulty, String role, Pageable pageable);
}
