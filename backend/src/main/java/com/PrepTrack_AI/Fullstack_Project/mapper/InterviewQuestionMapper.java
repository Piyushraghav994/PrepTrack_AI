package com.PrepTrack_AI.Fullstack_Project.mapper;

import com.PrepTrack_AI.Fullstack_Project.dto.InterviewQuestionRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.InterviewQuestionResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.InterviewQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterviewQuestionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "interview", ignore = true)
    InterviewQuestion toEntity(InterviewQuestionRequestDTO dto);

    @Mapping(source = "interview.id", target = "interviewId")
    InterviewQuestionResponseDTO toResponseDTO(InterviewQuestion entity);

    List<InterviewQuestionResponseDTO> toResponseDTOList(List<InterviewQuestion> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "interview", ignore = true)
    void updateEntityFromDto(InterviewQuestionRequestDTO dto, @MappingTarget InterviewQuestion entity);
}
