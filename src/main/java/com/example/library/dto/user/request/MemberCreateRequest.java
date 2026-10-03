package com.example.library.dto.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MemberCreateRequest {

    @NotNull
    private Long userId;

    @Size(max = 15)
    private String phone;

    @Size(max = 255)
    private String address;

    private BigDecimal amount;

}