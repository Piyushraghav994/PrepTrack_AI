package com.PrepTrack_AI.Fullstack_Project.repository;

import com.PrepTrack_AI.Fullstack_Project.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link InterviewSession} entities.
 */
@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    List<InterviewSession> findByUserIdOrderByStartTimeDesc(Long userId);
}
