package com.example.library.service.impl;

import com.example.library.dto.role_permission.request.RoleRequest;
import com.example.library.dto.role_permission.response.RoleResponse;
import com.example.library.entity.*;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.RoleMapper;
import com.example.library.repository.*;
import com.example.library.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());

        roleRepository.save(role);

        Set<RolePermission> rolePermissions = permissions.stream()
                .map(permission -> {
                    RolePermission rolePermission = new RolePermission();

                    RolePermissionId id = new RolePermissionId();
                    id.setRoleId(role.getId());
                    id.setPermissionId(permission.getId());

                    rolePermission.setId(id);
                    rolePermission.setRole(role);
                    rolePermission.setPermission(permission);

                    return rolePermission;
                })
                .collect(Collectors.toSet());

        role.setRolePermissions(rolePermissions);

        rolePermissionRepository.saveAll(rolePermissions);

        return roleMapper.toRoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(String id,RoleRequest request) {

        Role role = roleRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        // Xóa permission cũ
        rolePermissionRepository.deleteAllByRoleId(role.getId());

        if(!request.getPermissionIds().isEmpty()) {
            // Lấy permission mới
            List<Permission> permissions =
                    permissionRepository.findAllById(request.getPermissionIds());

            // Tạo RolePermission mới
            Set<RolePermission> rolePermissions = permissions.stream()
                    .map(permission -> {
                        RolePermissionId rolePermissionRepository = new RolePermissionId(
                                role.getId(),
                                permission.getId()
                        );

                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setId(rolePermissionRepository);
                        rolePermission.setRole(role);
                        rolePermission.setPermission(permission);

                        return rolePermission;
                    })
                    .collect(Collectors.toSet());

            rolePermissionRepository.saveAll(rolePermissions);

            role.setRolePermissions(rolePermissions);

        }

        return roleMapper.toRoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse deleteRole(String id) {

        Role role = roleRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        role.setIsDeleted(true);

        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getRoles() {

        return roleRepository.findAllByIsDeletedFalse()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }
}
