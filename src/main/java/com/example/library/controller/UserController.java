package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.user.request.UserRequest;
import com.example.library.dto.user.response.UserResponse;
import com.example.library.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(
            @RequestBody @Valid UserRequest request
    ) {
        return ApiResponse.success(
                userService.createUser(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest request
    ) {
        return ApiResponse.success(
                userService.updateUser(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<UserResponse> deleteUser(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                userService.deleteUser(id)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                userService.getUser(id)
        );
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.success(
                userService.getUsers()
        );
    }

    @GetMapping("/deleted/{id}")
    public ApiResponse<UserResponse> getUserDeleted(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                userService.getUserDeleted(id)
        );
    }
}