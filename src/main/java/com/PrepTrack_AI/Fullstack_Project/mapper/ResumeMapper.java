package com.PrepTrack_AI.Fullstack_Project.mapper;

import com.PrepTrack_AI.Fullstack_Project.dto.ResumeAnalysisResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.ResumeResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.Resume;
import com.PrepTrack_AI.Fullstack_Project.entity.ResumeAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResumeMapper {

    @Mapping(source = "user.id", target = "userId")
    ResumeResponseDTO toResponseDTO(Resume entity);

    List<ResumeResponseDTO> toResponseDTOList(List<Resume> entities);

    @Mapping(source = "resume.id", target = "resumeId")
    ResumeAnalysisResponseDTO toAnalysisResponseDTO(ResumeAnalysis entity);
}
