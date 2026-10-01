package com.example.library.service.impl;

import com.example.library.dto.authentication.request.LoginRequest;
import com.example.library.dto.authentication.request.LogoutRequest;
import com.example.library.dto.authentication.request.RefreshTokenRequest;
import com.example.library.dto.authentication.response.LoginResponse;
import com.example.library.dto.authentication.response.RefreshTokenResponse;
import com.example.library.entity.RefreshToken;
import com.example.library.entity.User;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.repository.RefreshTokenRepository;
import com.example.library.repository.UserRepository;
import com.example.library.security.PasswordEncoderConfig;
import com.example.library.service.AuthenticationService;
import com.example.library.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refresh_expiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user =
                userRepository
                        .findByEmailAndIsDeletedFalse(request.getEmail())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (Boolean.FALSE.equals(user.getIsActive())) throw new AppException(ErrorCode.USER_NOT_ACTIVE);
        boolean authenticated =
                passwordEncoderConfig
                        .passwordEncoder()
                        .matches(request.getPassword(), user.getPasswordHash());
        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var accessToken = jwtTokenService.generateAccessToken(user);
        var rawRefreshToken = jwtTokenService.generateRefreshToken(user);
        refreshTokenRepository.save(RefreshToken.builder()
                .token(rawRefreshToken)
                .user(user)
                .expiresAt(
                        LocalDateTime.now().plus(Duration.ofMillis(refresh_expiration))
                )
                .build());
        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .authorities(jwtTokenService.buildAuthorities(user))
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken).build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken()).orElse(null);
        assert refreshToken != null;
        if(Boolean.FALSE.equals(refreshToken.getRevoked())) throw new AppException(ErrorCode.UNAUTHENTICATED);
        User user = refreshToken.getUser();
        if(Boolean.TRUE.equals(user.getIsActive())) throw new AppException(ErrorCode.USER_NOT_ACTIVE);
        if(Boolean.TRUE.equals(user.getIsDeleted())) throw new AppException(ErrorCode.USER_NOT_FOUND);

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        var accessToken = jwtTokenService.generateAccessToken(user);
        var rawRefreshToken = jwtTokenService.generateRefreshToken(user);
        refreshTokenRepository.save(RefreshToken.builder()
                .token(rawRefreshToken)
                .user(user)
                .expiresAt(
                        LocalDateTime.now().plus(Duration.ofMillis(refresh_expiration))
                )
                .build());

        return RefreshTokenResponse.builder().accessToken(accessToken).refreshToken(rawRefreshToken).build();
    }

    @Override
    public void logout(LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken()).orElse(null);
        assert refreshToken != null;
        if(Boolean.TRUE.equals(refreshToken.getRevoked())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        }

    }
}
