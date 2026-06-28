package com.PrepTrack_AI.Fullstack_Project.repository;

import com.PrepTrack_AI.Fullstack_Project.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link InterviewQuestion} entities.
 */
@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByInterviewId(Long interviewId);
}
