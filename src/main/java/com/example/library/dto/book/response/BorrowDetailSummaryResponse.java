package com.example.library.dto.book.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowDetailSummaryResponse {
    long total;
    long borrowing;
    long overdue;
    long returned;
}