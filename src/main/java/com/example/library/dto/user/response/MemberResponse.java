package com.example.library.dto.user.response;

import com.example.library.entity.enums.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MemberResponse {

    private Long id;

    private UserResponse user;

    private String memberCode;

    private String phone;

    private String address;

    private LocalDate cardExpiry;

    private CardStatus cardStatus;

    private Boolean isDeleted;

    private String identityNumber;

    private LocalDateTime createdAt;
}