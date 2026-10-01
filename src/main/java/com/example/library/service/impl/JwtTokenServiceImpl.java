package com.example.library.service.impl;

import com.example.library.entity.User;
import com.example.library.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expiration-ms}")
    private Long access_expiration;

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refresh_expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateAccessToken(User user) {

        Date now = new Date();

        Date expiration = new Date(now.getTime() + access_expiration);

        List<String> authorities = buildAuthorities(user);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(
                        "scope",
                        String.join(" ", authorities)
                )
                .claim("type", "access")
                .issuer("library")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(
                        getSigningKey(),
                        Jwts.SIG.HS512
                )
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {

        Date now = new Date();

        Date expiration = new Date(now.getTime() + refresh_expiration);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("type", "refresh")
                .issuer("library")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(
                        getSigningKey(),
                        Jwts.SIG.HS512
                )
                .compact();
    }

    @Override
    public Claims validateToken(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public List<String> getAuthorities(Claims claims) {

        String scope = claims.get(
                "scope",
                String.class
        );

        if (scope == null || scope.isBlank()) {
            return List.of();
        }

        return List.of(scope.split(" "));
    }

    public List<String> buildAuthorities(User user) {
        if (user.getRole() == null) {
            return List.of();
        }

        List<String> authorities = new ArrayList<>();

        // Role
        authorities.add("ROLE_" + user.getRole().getName());

        // Permissions
        if (user.getRole().getRolePermissions() != null) {
            user.getRole().getRolePermissions()
                    .forEach(permission ->
                            authorities.add(permission.getPermission().getCode())
                    );
        }

        return authorities;
    }
}