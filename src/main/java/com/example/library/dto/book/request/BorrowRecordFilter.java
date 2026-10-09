package com.example.library.dto.book.request;

import com.example.library.dto.book.request.enum_request.BorrowRecordStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordFilter {
    String memberKeyword;
    String bookKeyword;
    LocalDate startBorrowDate;
    LocalDate endBorrowDate;
    BorrowRecordStatus status;
}
