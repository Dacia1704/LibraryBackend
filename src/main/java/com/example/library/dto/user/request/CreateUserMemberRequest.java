package com.example.library.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateUserMemberRequest {

    // ===== User info =====
    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    private String avatar;

    // ===== Member info =====
    @NotBlank
    @Size(max = 12)
    private String identityNumber;

    @Size(max = 15)
    private String phone;

    @Size(max = 255)
    private String address;

    @NotNull
    @Min(value = 1, message = "Số tháng đăng ký phải lớn hơn 0")
    private Integer monthRequest;

    private BigDecimal amount;
}