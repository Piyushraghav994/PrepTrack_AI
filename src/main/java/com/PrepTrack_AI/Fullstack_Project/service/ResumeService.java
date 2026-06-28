package com.PrepTrack_AI.Fullstack_Project.service;

import com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeAnalysisResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeResponseDTO;

import java.util.List;

/**
 * Service interface for Resume uploads and ATS Resume Analysis.
 */
public interface ResumeService {

    ApiResponse<ResumeResponseDTO> uploadAndAnalyze(String email, ResumeRequestDTO request);

    ApiResponse<List<ResumeResponseDTO>> getUserResumes(String email);

    ApiResponse<ResumeAnalysisResponseDTO> getResumeAnalysis(Long resumeId);
}
