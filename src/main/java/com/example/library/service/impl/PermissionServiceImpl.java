package com.example.library.service.impl;

import com.example.library.dto.role_permission.request.PermissionRequest;
import com.example.library.dto.role_permission.response.PermissionResponse;
import com.example.library.entity.Permission;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.PermissionMapper;
import com.example.library.repository.PermissionRepository;
import com.example.library.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {

        Permission permission = permissionMapper.toPermission(request);

        permission.setIsDeleted(false);

        permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(String id,PermissionRequest request) {

        Permission permission = permissionRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() ->
                        new AppException(ErrorCode.PERMISSION_NOT_FOUND)
                );

        permission.setCode(request.getCode());
        permission.setDescription(request.getDescription());

        permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    @Transactional
    public PermissionResponse deletePermission(String id) {

        Permission permission = permissionRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() ->
                        new AppException(ErrorCode.PERMISSION_NOT_FOUND)
                );

        permission.setIsDeleted(true);

        permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissions() {

        return permissionRepository.findAllByIsDeletedFalse()
                .stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }
}