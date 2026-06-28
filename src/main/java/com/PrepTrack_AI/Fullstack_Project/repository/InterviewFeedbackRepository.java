package com.PrepTrack_AI.Fullstack_Project.repository;

import com.PrepTrack_AI.Fullstack_Project.entity.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link InterviewFeedback} entities.
 */
@Repository
public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, Long> {
    Optional<InterviewFeedback> findBySessionId(Long sessionId);
}
