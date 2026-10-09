package com.example.library.dto.book.response;

import com.example.library.dto.user.response.MemberResponse;
import com.example.library.dto.user.response.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordResponse {
    Long id;

    MemberResponse member;

    UserResponse librarian;

    LocalDate borrowDate;

    LocalDate dueDate;

    String note;

    List<BorrowDetailResponseForRecord> borrowDetails;

}
