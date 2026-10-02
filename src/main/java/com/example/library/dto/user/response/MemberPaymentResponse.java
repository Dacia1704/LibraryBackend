package com.example.library.dto.user.response;

import com.example.library.entity.enums.PaymentType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberPaymentResponse {

    Long id;

    Long memberId;

    String memberCode;

    String memberName;

    BigDecimal amount;

    PaymentType paymentType;

    LocalDateTime paidAt;

    Long receivedBy;

    String receivedByUsername;

    String note;

    Boolean isDeleted;
}