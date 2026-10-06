package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.BorrowRecordFilter;
import com.example.library.dto.book.request.BorrowRecordRequest;
import com.example.library.dto.book.response.BorrowRecordResponse;

import java.util.List;

public interface BorrowRecordService {

    BorrowRecordResponse createBorrowRecord(BorrowRecordRequest request);

    BorrowRecordResponse updateBorrowRecord(Long id, BorrowRecordRequest request);

    BorrowRecordResponse deleteBorrowRecord(String id);

    BorrowRecordResponse getBorrowRecord(String id);

    List<BorrowRecordResponse> getBorrowRecords();

    PageResponse<BorrowRecordResponse> getBorrowRecordsPagination(
            BorrowRecordFilter filter,
            int page,
            int size
    );
}