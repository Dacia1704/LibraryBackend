package com.example.library.service.impl;

import com.example.library.dto.book.response.BorrowDetailResponse;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowDetail;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.BorrowDetailMapper;
import com.example.library.repository.BorrowDetailRepository;
import com.example.library.service.BorrowDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowDetailServiceImpl implements BorrowDetailService {

    BorrowDetailRepository borrowDetailRepository;

    BorrowDetailMapper borrowDetailMapper;

    @Override
    @Transactional(readOnly = true)
    public BorrowDetailResponse getBorrowDetail(String id) {

        BorrowDetail borrowDetail =
                borrowDetailRepository
                        .findByIdAndIsDeletedFalse(Long.valueOf(id))
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.BORROW_DETAIL_NOT_FOUND
                                )
                        );

        return borrowDetailMapper.toBorrowDetailResponse(
                borrowDetail
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowDetailResponse> getBorrowDetails() {

        return borrowDetailRepository
                .findAllByIsDeletedFalse()
                .stream()
                .map(borrowDetailMapper::toBorrowDetailResponse)
                .toList();
    }

    @Override
    @Transactional
    public BorrowDetailResponse returnBook(String id) {

        BorrowDetail borrowDetail =
                borrowDetailRepository
                        .findByIdAndIsDeletedFalse(Long.valueOf(id))
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.BORROW_DETAIL_NOT_FOUND
                                )
                        );

        // Đã trả rồi thì không cho trả lại
        if (borrowDetail.getReturnDate() != null) {
            throw new AppException(
                    ErrorCode.BOOK_ALREADY_RETURNED
            );
        }

        LocalDate returnDate = LocalDate.now();

        borrowDetail.setReturnDate(returnDate);

        // Tính tiền phạt
        LocalDate dueDate = borrowDetail.getBorrowRecord().getDueDate();

        if (returnDate.isAfter(dueDate)) {

            long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);

            BigDecimal fine = BigDecimal.valueOf(lateDays).multiply(BigDecimal.valueOf(10_000));

            borrowDetail.setFineAmount(fine);

        } else {
            borrowDetail.setFineAmount(BigDecimal.ZERO);
        }

        Book book = borrowDetail.getBook();
        book.setAvailable(book.getAvailable() + 1);

        borrowDetailRepository.save(borrowDetail);

        return borrowDetailMapper.toBorrowDetailResponse(borrowDetail);
    }
}