package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProgressResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.User;

/**
 * Service interface for tracking user progress and scores.
 */
public interface UserProgressService {

    ApiResponse<UserProgressResponseDTO> getUserProgress(String email);

    void updateProgressAfterSession(User user, int score, int questionsCount);

    void updateProgressAfterResume(User user, int atsScore);
}
