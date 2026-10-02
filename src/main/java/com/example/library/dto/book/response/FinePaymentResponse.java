package com.example.library.dto.book.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinePaymentResponse {

    private Long id;

    private Long memberId;

    private BigDecimal amount;

    private LocalDateTime paidAt;

    private Long receivedBy;

    private String receivedByUsername;

    private String note;

    private Boolean isDeleted;
}