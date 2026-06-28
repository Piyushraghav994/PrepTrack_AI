package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProgressResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.entity.UserProgress;
import com.PrepTrack_AI.Fullstack_Project.exception.UserNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.mapper.UserProgressMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.UserProgressRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link UserProgressService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserProgressServiceImpl implements UserProgressService {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final UserProgressMapper userProgressMapper;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UserProgressResponseDTO> getUserProgress(String email) {
        log.debug("Retrieving progress for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        UserProgress progress = getOrCreateUserProgress(user);
        return ApiResponse.success("User progress retrieved successfully", userProgressMapper.toResponseDTO(progress));
    }

    @Override
    public void updateProgressAfterSession(User user, int score, int questionsCount) {
        log.info("Updating progress for user: {} after completing interview session with score: {}, questions: {}", 
                user.getEmail(), score, questionsCount);
        UserProgress progress = getOrCreateUserProgress(user);

        progress.setCompletedInterviews(progress.getCompletedInterviews() + 1);
        progress.setCompletedQuestions(progress.getCompletedQuestions() + questionsCount);
        progress.setTotalScore(progress.getTotalScore() + score);
        progress.setCurrentStreak(progress.getCurrentStreak() + 1);

        userProgressRepository.save(progress);
        log.info("Progress updated successfully. Total score: {}, current streak: {}", progress.getTotalScore(), progress.getCurrentStreak());
    }

    @Override
    public void updateProgressAfterResume(User user, int atsScore) {
        log.info("Updating progress for user: {} after resume upload. ATS score: {}", user.getEmail(), atsScore);
        UserProgress progress = getOrCreateUserProgress(user);

        progress.setTotalScore(progress.getTotalScore() + atsScore);
        progress.setCurrentStreak(progress.getCurrentStreak() + 1);

        userProgressRepository.save(progress);
        log.info("Progress updated successfully. Total score: {}, current streak: {}", progress.getTotalScore(), progress.getCurrentStreak());
    }

    private UserProgress getOrCreateUserProgress(User user) {
        return userProgressRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    log.info("Creating new UserProgress tracker for user: {}", user.getEmail());
                    UserProgress newProgress = UserProgress.builder()
                            .user(user)
                            .completedQuestions(0)
                            .completedInterviews(0)
                            .currentStreak(0)
                            .totalScore(0)
                            .build();
                    return userProgressRepository.save(newProgress);
                });
    }
}
