package com.PrepTrack_AI.Fullstack_Project.mapper;

import com.PrepTrack_AI.Fullstack_Project.dto.UserRequestDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserResponseDTO;
import com.PrepTrack_AI.Fullstack_Project.dto.UserProfileDTO;
import com.PrepTrack_AI.Fullstack_Project.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRequestDTO dto);

    @Mapping(source = "role.name", target = "role")
    UserResponseDTO toResponseDTO(User user);

    @Mapping(source = "role.name", target = "role")
    UserProfileDTO toProfileDTO(User user);

    List<UserResponseDTO> toResponseDTOList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateUserFromDto(UserRequestDTO dto, @MappingTarget User user);
}
