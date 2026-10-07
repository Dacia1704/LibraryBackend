package com.example.library.mapper;

import com.example.library.dto.user.request.UserRequest;
import com.example.library.dto.user.response.UserResponse;
import com.example.library.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "noAccent", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "failedAttempts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    User toUser(UserRequest request);

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target= "member", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "noAccent", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "failedAttempts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    void updateUser(
            @MappingTarget User user,
            UserRequest request
    );
}