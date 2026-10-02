package com.example.library.dto.book.request;

import com.example.library.entity.enums.BorrowStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordRequest {

    @NotNull
    String memberId;

    @NotNull
    String librarianId;

    @NotNull
    LocalDate borrowDate;

    @NotNull
    LocalDate dueDate;

    BorrowStatus status;

    String note;

    @NotEmpty
    @NotNull
    List<String> bookIds;

}
