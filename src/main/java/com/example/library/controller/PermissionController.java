package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.role_permission.request.PermissionRequest;
import com.example.library.dto.role_permission.response.PermissionResponse;
import com.example.library.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@RequestBody @Valid PermissionRequest request) {
        return ApiResponse.success(permissionService.createPermission(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> updatePermission(@PathVariable String id,@RequestBody @Valid PermissionRequest request) {
        return ApiResponse.success(permissionService.updatePermission(id,request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<PermissionResponse> deletePermission(@PathVariable String id) {
        return ApiResponse.success(permissionService.deletePermission(id));
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getPermissions() {
        return ApiResponse.success(permissionService.getPermissions());
    }
}