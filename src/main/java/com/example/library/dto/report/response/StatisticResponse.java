package com.example.library.dto.report.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticResponse {

    // Borrow Record Statistics
    Long totalBorrowRecords;
    Long borrowingRecords;
    Long returnedRecords;
    Long overdueRecords;

    // Borrow Detail Statistics
    Long totalBorrowDetails;
    Long returnedBorrowDetails;
    Long notReturnedBorrowDetails;

    // Fine Statistics
    Long totalFines;
    Long unpaidFines;
    Long totalFinePayments;
    Long totalFineAmount;
    Long totalPaidAmount;
    Long totalRemainingAmount;

    // Book Statistics
    Long totalBooks;
    Long totalBookCopies;
    Long availableBookCopies;
    Long borrowedBookCopies;

    // Book Info Statistics
    Long totalAuthors;
    Long totalCategories;
    Long totalShelves;
    Long totalPublishers;

    // Reader Statistics
    Long totalReaders;
    Long activeReaders;
    Long inactiveReaders;

    // Member Statistics
    Long totalMembers;
    Long activeMembers;
    Long expiredMembers;

    // Librarian Statistics
    Long totalLibrarians;
    Long activeLibrarians;
    Long inactiveLibrarians;

    // Fine Amount Details
    BigDecimal totalFineAmountDetail;
    BigDecimal totalPaidAmountDetail;
    BigDecimal totalRemainingAmountDetail;
}
