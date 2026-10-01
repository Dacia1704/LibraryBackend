package com.example.library.service;

import com.example.library.dto.role_permission.request.PermissionRequest;
import com.example.library.dto.role_permission.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    public PermissionResponse createPermission(PermissionRequest request);
    public PermissionResponse updatePermission(String id,PermissionRequest request);
    public PermissionResponse deletePermission(String id);
    public List<PermissionResponse> getPermissions();
}
