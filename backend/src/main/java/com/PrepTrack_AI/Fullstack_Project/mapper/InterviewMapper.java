package com.PrepTrack_AI.Fullstack_Project.mapper;

import com.PrepTrack_AI.Fullstack_Project.dto.InterviewRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.InterviewResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.Interview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Interview toEntity(InterviewRequestDTO dto);

    InterviewResponseDTO toResponseDTO(Interview entity);

    List<InterviewResponseDTO> toResponseDTOList(List<Interview> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "questions", ignore = true)
    void updateEntityFromDto(InterviewRequestDTO dto, @MappingTarget Interview entity);
}
