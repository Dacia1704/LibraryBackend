package com.example.library.service;

import com.example.library.entity.User;
import io.jsonwebtoken.Claims;

import java.util.List;

public interface JwtTokenService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    Claims validateToken(String token);

    List<String> getAuthorities(Claims claims);

    List<String> buildAuthorities(User user);
}