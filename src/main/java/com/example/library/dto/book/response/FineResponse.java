package com.example.library.dto.book.response;

import com.example.library.entity.enums.FineReason;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineResponse {

    private Long id;

    private Long borrowId;

    private Long borrowDetailId;

    private BigDecimal amount;

    private FineReason reason;

    private Integer overdueDays;

    private String note;

    private LocalDateTime createdAt;

    private Boolean isDeleted;
}