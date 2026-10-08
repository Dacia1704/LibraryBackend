package com.example.library.dto.user.request;

import com.example.library.entity.enums.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemberRequest {

    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 20)
    private String memberCode;

    @NotBlank
    @Size(max = 12)
    private String identityNumber;

    @Size(max = 15)
    private String phone;

    private CardStatus cardStatus;

    @Size(max = 255)
    private String address;

    @NotNull
    private LocalDate cardExpiry;
}