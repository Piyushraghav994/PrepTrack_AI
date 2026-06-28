package com.PrepTrack_AI.Fullstack_Project.repository;

import com.PrepTrack_AI.Fullstack_Project.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link ResumeAnalysis} entities.
 */
@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    Optional<ResumeAnalysis> findByResumeId(Long resumeId);
}
