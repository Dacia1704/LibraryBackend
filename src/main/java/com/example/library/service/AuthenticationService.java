package com.example.library.service;

import com.example.library.dto.authentication.request.LoginRequest;
import com.example.library.dto.authentication.request.LogoutRequest;
import com.example.library.dto.authentication.request.RefreshTokenRequest;
import com.example.library.dto.authentication.response.LoginResponse;
import com.example.library.dto.authentication.response.RefreshTokenResponse;

public interface AuthenticationService {
    public LoginResponse login(LoginRequest request);
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    public void logout(LogoutRequest request);
}
