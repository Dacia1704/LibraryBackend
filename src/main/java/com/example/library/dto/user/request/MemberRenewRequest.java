package com.example.library.dto.user.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MemberRenewRequest {

    private BigDecimal amount;

    @Min(value = 1, message = "Số tháng phải >= 1")
    private Integer monthRequest = 1;  // Mặc định 1 tháng nếu không truyền
}