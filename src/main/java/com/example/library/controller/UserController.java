package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.user.request.UserActiveRequest;
import com.example.library.dto.user.request.UserFilter;
import com.example.library.dto.user.request.UserRequest;
import com.example.library.dto.user.response.UserResponse;
import com.example.library.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ApiResponse<UserResponse> createUser(
            @RequestBody @Valid UserRequest request
    ) {
        return ApiResponse.success(
                userService.createUser(request)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest request
    ) {
        return ApiResponse.success(
                userService.updateUser(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ApiResponse<UserResponse> deleteUser(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                userService.deleteUser(id)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<UserResponse> getUser(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                userService.getUser(id)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<PageResponse<UserResponse>> getUsers(
            @ModelAttribute UserFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .data(
                        userService.getUsers(
                                filter,
                                page,
                                size
                        )
                )
                .build();
    }

    @GetMapping("/deleted/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<UserResponse> getUserDeleted(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                userService.getUserDeleted(id)
        );
    }

    @PutMapping("/{id}/active")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ApiResponse<UserResponse> setUserActive(
            @PathVariable Long id,
            @RequestBody UserActiveRequest request
    ) {
        return ApiResponse.success(
                userService.setUserActive(id, request)
        );
    }
}