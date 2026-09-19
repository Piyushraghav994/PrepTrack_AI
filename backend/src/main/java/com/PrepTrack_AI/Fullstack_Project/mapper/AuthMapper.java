package com.PrepTrack_AI.Fullstack_Project.mapper;

import com.PrepTrack_AI.Fullstack_Project.dto.RegisterRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.AuthResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.role.name", target = "role")
    @Mapping(source = "accessToken", target = "token")
    @Mapping(source = "refreshToken", target = "refreshToken")
    @Mapping(target = "tokenType", constant = "Bearer")
    AuthResponseDTO toAuthResponse(User user, String accessToken, String refreshToken);
}
