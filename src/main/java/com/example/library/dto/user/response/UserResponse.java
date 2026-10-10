package com.example.library.dto.user.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {

    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String avatar;

    private Long roleId;

    private String roleName;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private MemberResponse member;
}