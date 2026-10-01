package com.example.library.mapper;

import com.example.library.dto.role_permission.request.PermissionRequest;
import com.example.library.dto.role_permission.response.PermissionResponse;
import com.example.library.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toPermissionResponse(Permission permission);

    Permission toPermission(PermissionRequest request);
}
