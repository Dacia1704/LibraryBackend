package com.example.library.dto.user.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemberResponse {

    private Long id;

    private UserResponse user;

    private String memberCode;

    private String phone;

    private String address;

    private LocalDate cardExpiry;

    private Boolean isDeleted;
}