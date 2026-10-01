package com.example.library.mapper;

import com.example.library.dto.role_permission.request.RoleRequest;
import com.example.library.dto.role_permission.response.RoleResponse;
import com.example.library.entity.Permission;
import com.example.library.entity.Role;
import com.example.library.entity.RolePermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(
            target = "permissionIds",
            source = "rolePermissions"
    )
    RoleResponse toRoleResponse(Role role);

    Role toRole(RoleRequest request);

    default List<Long> mapPermissionIds(
            Set<RolePermission> rolePermissions
    ) {
        if (rolePermissions == null) {
            return List.of();
        }

        return rolePermissions.stream()
                .map(RolePermission::getPermission)
                .filter(Objects::nonNull)
                .map(Permission::getId)
                .toList();
    }
}