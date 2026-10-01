package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.authentication.request.LoginRequest;
import com.example.library.dto.authentication.request.LogoutRequest;
import com.example.library.dto.authentication.request.RefreshTokenRequest;
import com.example.library.dto.authentication.response.LoginResponse;
import com.example.library.dto.authentication.response.RefreshTokenResponse;
import com.example.library.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authenticationService.login(request));
    }

    @PostMapping("/refresh")
    ApiResponse<RefreshTokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ApiResponse.success(authenticationService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestBody @Valid LogoutRequest request) {
        authenticationService.logout(request);
        return ApiResponse.success("Đăng xuất thành công");
    }
}
