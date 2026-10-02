package com.example.library.dto.book.response;

import com.example.library.entity.BorrowDetail;
import com.example.library.entity.Member;
import com.example.library.entity.User;
import com.example.library.entity.enums.BorrowStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowDetailResponse {
    Long id;
    BookResponse bookResponse;
    LocalDate returnDate;
    BigDecimal fineAmount;
}
