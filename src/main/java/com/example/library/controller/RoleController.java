package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.role_permission.request.RoleRequest;
import com.example.library.dto.role_permission.response.RoleResponse;
import com.example.library.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody @Valid RoleRequest request) {
        return ApiResponse.success(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable String id, @RequestBody @Valid RoleRequest request) {
        return ApiResponse.success(roleService.updateRole(id,request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<RoleResponse> deleteRole(@PathVariable String id) {
        return ApiResponse.success(roleService.deleteRole(id));
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.success(roleService.getRoles());
    }
}