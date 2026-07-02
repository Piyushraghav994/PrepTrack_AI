package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeAnalysisResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.PagedResponse;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

/**
 * Service interface for Resume uploads and ATS Resume Analysis.
 */
public interface ResumeService {

    ApiResponse<ResumeResponseDTO> uploadAndAnalyze(String email, ResumeRequestDTO request);

    ApiResponse<ResumeResponseDTO> uploadAndAnalyzeFile(String email, MultipartFile file) throws IOException;

    ApiResponse<PagedResponse<ResumeResponseDTO>> getUserResumes(String email, int page, int size);

    ApiResponse<ResumeAnalysisResponseDTO> getResumeAnalysis(Long resumeId);
}
