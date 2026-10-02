package com.example.library.dto.book.request;

import com.example.library.entity.enums.BorrowStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordFilter {
    Long memberId;
    LocalDate borrowDate;
    BorrowStatus status;
    Long bookId;
}
