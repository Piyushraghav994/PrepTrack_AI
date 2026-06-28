package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeAnalysisResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.Resume;
import com.PrepTrack_AI.Fullstack_Project.entity.ResumeAnalysis;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import com.PrepTrack_AI.Fullstack_Project.exception.ResourceNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.exception.UserNotFoundException;
import com.PrepTrack_AI.Fullstack_Project.mapper.ResumeMapper;
import com.PrepTrack_AI.Fullstack_Project.repository.ResumeAnalysisRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.ResumeRepository;
import com.PrepTrack_AI.Fullstack_Project.repository.UserRepository;
import com.PrepTrack_AI.Fullstack_Project.service.ResumeService;
import com.PrepTrack_AI.Fullstack_Project.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the {@link ResumeService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeMapper resumeMapper;
    private final UserProgressService userProgressService;
    private final Random random = new Random();

    @Override
    public ApiResponse<ResumeResponseDTO> uploadAndAnalyze(String email, ResumeRequestDTO request) {
        log.info("Uploading resume for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Resume resume = Resume.builder()
                .fileUrl(request.getFileUrl())
                .uploadedAt(LocalDateTime.now())
                .user(user)
                .build();

        Resume savedResume = resumeRepository.save(resume);
        log.info("Resume saved successfully with ID: {}", savedResume.getId());

        // Perform mock ATS analysis
        ResumeAnalysis analysis = generateMockAnalysis(savedResume);
        resumeAnalysisRepository.save(analysis);

        // Update User Progress (award 15 points for uploading resume and update streak)
        userProgressService.updateProgressAfterResume(user, analysis.getAtsScore());

        return ApiResponse.success("Resume uploaded and analyzed successfully", resumeMapper.toResponseDTO(savedResume));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ResumeResponseDTO>> getUserResumes(String email) {
        log.debug("Fetching resumes for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        List<Resume> resumes = resumeRepository.findByUserIdOrderByUploadedAtDesc(user.getId());
        return ApiResponse.success("User resumes fetched successfully", resumeMapper.toResponseDTOList(resumes));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ResumeAnalysisResponseDTO> getResumeAnalysis(Long resumeId) {
        log.debug("Fetching analysis for resume ID: {}", resumeId);
        if (!resumeRepository.existsById(resumeId)) {
            throw new ResourceNotFoundException("Resume", "id", resumeId);
        }

        ResumeAnalysis analysis = resumeAnalysisRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("ResumeAnalysis not found for resume ID: " + resumeId));

        return ApiResponse.success("Resume analysis fetched successfully", resumeMapper.toAnalysisResponseDTO(analysis));
    }

    private ResumeAnalysis generateMockAnalysis(Resume resume) {
        // Generate score between 65 and 95
        int score = 65 + random.nextInt(31);

        String skills = "Java, Spring Boot, MySQL, Hibernate, REST APIs, Git, Maven, HTML/CSS, JavaScript";
        String missingKeywords;
        String suggestions;

        if (score >= 85) {
            missingKeywords = "Docker, Kubernetes, AWS, CI/CD Pipelines";
            suggestions = "Your resume has an outstanding ATS format and density. " +
                    "To further enhance it, add specific examples of using cloud environments (AWS/Azure) and automated testing frameworks (JUnit, Mockito).";
        } else if (score >= 70) {
            missingKeywords = "Microservices, Spring Cloud, Redis, Unit Testing (JUnit), Docker";
            suggestions = "Your resume structure is good but lacks quantitative metrics. " +
                    "Add statistics showing the impact of your work (e.g., 'improved query performance by 20%'). " +
                    "Include cloud and devops skills if applicable to make it stand out.";
        } else {
            missingKeywords = "Data Structures, System Design, Spring Boot Security, JPA, AWS, Unit Testing";
            suggestions = "Consider rebuilding your resume layout using a single-column, standard ATS-friendly template. " +
                    "Structure your experience with strong action verbs. Detail your projects by mentioning specific technologies used " +
                    "and the problems you solved.";
        }

        return ResumeAnalysis.builder()
                .resume(resume)
                .atsScore(score)
                .skills(skills)
                .missingKeywords(missingKeywords)
                .suggestions(suggestions)
                .build();
    }
}
