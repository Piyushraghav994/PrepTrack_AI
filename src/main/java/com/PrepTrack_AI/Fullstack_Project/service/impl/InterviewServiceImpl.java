package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.*;
import com.PrepTrack_AI.Fullstack_Project.entity.Difficulty;
import com.PrepTrack_AI.Fullstack_Project.entity.Interview;
import com.PrepTrack_AI.Fullstack_Project.entity.InterviewQuestion;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.mapper.InterviewMapper;
import com.PrepTrack_AI.Fullstack_Project.mapper.InterviewQuestionMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewQuestionRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.InterviewRepository;
import com.PrepTrack_AI.Fullstack_Project.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link InterviewService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewMapper interviewMapper;
    private final InterviewQuestionMapper interviewQuestionMapper;

    @Override
    public ApiResponse<InterviewResponseDTO> createInterview(InterviewRequestDTO request) {
        log.info("Creating new interview: {}", request.getTitle());
        Interview interview = interviewMapper.toEntity(request);
        Interview saved = interviewRepository.save(interview);
        log.info("Interview created successfully with ID: {}", saved.getId());
        return ApiResponse.success("Interview created successfully", interviewMapper.toResponseDTO(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<InterviewResponseDTO> getInterviewById(Long id) {
        log.debug("Fetching interview with ID: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", id));
        return ApiResponse.success("Interview retrieved successfully", interviewMapper.toResponseDTO(interview));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<InterviewResponseDTO>> getAllInterviews(Difficulty difficulty, String role) {
        log.debug("Fetching all interviews. Filter by difficulty: {}, role: {}", difficulty, role);
        List<Interview> interviews;
        if (difficulty != null) {
            interviews = interviewRepository.findByDifficulty(difficulty);
        } else {
            interviews = interviewRepository.findAll();
        }

        if (role != null && !role.isBlank()) {
            interviews = interviews.stream()
                    .filter(i -> i.getRole().toLowerCase().contains(role.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<InterviewResponseDTO> dtos = interviewMapper.toResponseDTOList(interviews);
        return ApiResponse.success("Interviews retrieved successfully", dtos);
    }

    @Override
    public ApiResponse<InterviewResponseDTO> updateInterview(Long id, InterviewRequestDTO request) {
        log.info("Updating interview with ID: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", id));
        interviewMapper.updateEntityFromDto(request, interview);
        Interview updated = interviewRepository.save(interview);
        log.info("Interview updated successfully with ID: {}", updated.getId());
        return ApiResponse.success("Interview updated successfully", interviewMapper.toResponseDTO(updated));
    }

    @Override
    public ApiResponse<Void> deleteInterview(Long id) {
        log.info("Deleting interview with ID: {}", id);
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", id));
        interviewRepository.delete(interview);
        log.info("Interview deleted successfully with ID: {}", id);
        return ApiResponse.success("Interview deleted successfully");
    }

    @Override
    public ApiResponse<InterviewQuestionResponseDTO> addQuestionToInterview(InterviewQuestionRequestDTO request) {
        log.info("Adding question to interview with ID: {}", request.getInterviewId());
        Interview interview = interviewRepository.findById(request.getInterviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", request.getInterviewId()));

        InterviewQuestion question = interviewQuestionMapper.toEntity(request);
        question.setInterview(interview);

        InterviewQuestion saved = interviewQuestionRepository.save(question);
        log.info("Question added successfully with ID: {}", saved.getId());
        return ApiResponse.success("Question added successfully", interviewQuestionMapper.toResponseDTO(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<InterviewQuestionResponseDTO>> getQuestionsByInterview(Long interviewId) {
        log.debug("Fetching questions for interview ID: {}", interviewId);
        if (!interviewRepository.existsById(interviewId)) {
            throw new ResourceNotFoundException("Interview", "id", interviewId);
        }
        List<InterviewQuestion> questions = interviewQuestionRepository.findByInterviewId(interviewId);
        List<InterviewQuestionResponseDTO> dtos = interviewQuestionMapper.toResponseDTOList(questions);
        return ApiResponse.success("Questions retrieved successfully", dtos);
    }

    @Override
    public ApiResponse<InterviewQuestionResponseDTO> updateQuestion(Long questionId, InterviewQuestionRequestDTO request) {
        log.info("Updating question with ID: {}", questionId);
        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (!question.getInterview().getId().equals(request.getInterviewId())) {
            Interview newInterview = interviewRepository.findById(request.getInterviewId())
                    .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", request.getInterviewId()));
            question.setInterview(newInterview);
        }

        interviewQuestionMapper.updateEntityFromDto(request, question);
        InterviewQuestion updated = interviewQuestionRepository.save(question);
        log.info("Question updated successfully with ID: {}", updated.getId());
        return ApiResponse.success("Question updated successfully", interviewQuestionMapper.toResponseDTO(updated));
    }

    @Override
    public ApiResponse<Void> deleteQuestion(Long questionId) {
        log.info("Deleting question with ID: {}", questionId);
        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
        interviewQuestionRepository.delete(question);
        log.info("Question deleted successfully with ID: {}", questionId);
        return ApiResponse.success("Question deleted successfully");
    }
}
