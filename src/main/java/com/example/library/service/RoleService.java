package com.example.library.service;

import com.example.library.dto.role_permission.request.RoleRequest;
import com.example.library.dto.role_permission.response.RoleResponse;

import java.util.List;

public interface RoleService {
    public RoleResponse createRole(RoleRequest request);
    public RoleResponse updateRole(String id,RoleRequest request);
    public RoleResponse deleteRole(String id);
    public List<RoleResponse> getRoles();
}
