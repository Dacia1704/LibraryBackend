package com.example.library.dto.book.response;

import com.example.library.entity.enums.BorrowStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowDetailResponse {
    Long id;
    BookResponse book;
    LocalDate returnDate;
    BigDecimal fineAmount;
    BorrowStatus borrowStatus;
    BorrowRecordResponse borrowRecord;
}
