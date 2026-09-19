package com.PrepTrack_AI.Fullstack_Project.mapper;

import com.PrepTrack_AI.Fullstack_Project.dto.InterviewFeedbackResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.InterviewSessionResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.InterviewFeedback;
import com.PrepTrack_AI.Fullstack_Project.entity.InterviewSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterviewSessionMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "interview.id", target = "interviewId")
    @Mapping(source = "interview.title", target = "interviewTitle")
    InterviewSessionResponseDTO toResponseDTO(InterviewSession entity);

    List<InterviewSessionResponseDTO> toResponseDTOList(List<InterviewSession> entities);

    @Mapping(source = "session.id", target = "sessionId")
    InterviewFeedbackResponseDTO toFeedbackResponseDTO(InterviewFeedback entity);
}
