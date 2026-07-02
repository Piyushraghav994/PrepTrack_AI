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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ApiResponse<PagedResponse<InterviewResponseDTO>> getAllInterviews(Difficulty difficulty, String role, int page, int size) {
        log.debug("Fetching all interviews. Filter by difficulty: {}, role: {}, page: {}, size: {}", difficulty, role, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Interview> interviewPage;

        boolean hasRole = role != null && !role.isBlank();

        if (difficulty != null && hasRole) {
            interviewPage = interviewRepository.findByDifficultyAndRoleContainingIgnoreCase(difficulty, role, pageable);
        } else if (difficulty != null) {
            interviewPage = interviewRepository.findByDifficulty(difficulty, pageable);
        } else if (hasRole) {
            interviewPage = interviewRepository.findByRoleContainingIgnoreCase(role, pageable);
        } else {
            interviewPage = interviewRepository.findAll(pageable);
        }

        List<InterviewResponseDTO> content = interviewMapper.toResponseDTOList(interviewPage.getContent());

        PagedResponse<InterviewResponseDTO> response = PagedResponse.<InterviewResponseDTO>builder()
                .content(content)
                .pageNumber(interviewPage.getNumber())
                .pageSize(interviewPage.getSize())
                .totalElements(interviewPage.getTotalElements())
                .totalPages(interviewPage.getTotalPages())
                .last(interviewPage.isLast())
                .build();

        return ApiResponse.success("Interviews retrieved successfully", response);
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
    public ApiResponse<PagedResponse<InterviewQuestionResponseDTO>> getQuestionsByInterview(Long interviewId, int page, int size) {
        log.debug("Fetching questions for interview ID: {}, page: {}, size: {}", interviewId, page, size);
        if (!interviewRepository.existsById(interviewId)) {
            throw new ResourceNotFoundException("Interview", "id", interviewId);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<InterviewQuestion> questionPage = interviewQuestionRepository.findByInterviewId(interviewId, pageable);
        List<InterviewQuestionResponseDTO> content = interviewQuestionMapper.toResponseDTOList(questionPage.getContent());

        PagedResponse<InterviewQuestionResponseDTO> response = PagedResponse.<InterviewQuestionResponseDTO>builder()
                .content(content)
                .pageNumber(questionPage.getNumber())
                .pageSize(questionPage.getSize())
                .totalElements(questionPage.getTotalElements())
                .totalPages(questionPage.getTotalPages())
                .last(questionPage.isLast())
                .build();

        return ApiResponse.success("Questions retrieved successfully", response);
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
