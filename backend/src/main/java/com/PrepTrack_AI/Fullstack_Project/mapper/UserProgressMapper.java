package com.PrepTrack_AI.Fullstack_Project.mapper;

import com.PrepTrack_AI.Fullstack_Project.dto.UserProgressResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.UserProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProgressMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    UserProgressResponseDTO toResponseDTO(UserProgress entity);
}
