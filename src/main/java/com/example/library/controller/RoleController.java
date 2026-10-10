package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.role_permission.request.RoleRequest;
import com.example.library.dto.role_permission.response.RoleResponse;
import com.example.library.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ApiResponse<RoleResponse> createRole(@RequestBody @Valid RoleRequest request) {
        return ApiResponse.success(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ApiResponse<RoleResponse> updateRole(@PathVariable String id, @RequestBody @Valid RoleRequest request) {
        return ApiResponse.success(roleService.updateRole(id,request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ApiResponse<RoleResponse> deleteRole(@PathVariable String id) {
        return ApiResponse.success(roleService.deleteRole(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.success(roleService.getRoles());
    }
}