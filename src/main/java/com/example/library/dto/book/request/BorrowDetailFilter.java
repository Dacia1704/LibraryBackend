package com.example.library.dto.book.request;

import com.example.library.entity.enums.BorrowStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowDetailFilter {
    BorrowStatus status;
    String keyword;
    Integer year;
}