package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.BorrowDetailFilter;
import com.example.library.dto.book.request.ReturnBookRequest;
import com.example.library.dto.book.response.BorrowDetailResponse;

import java.util.List;

public interface BorrowDetailService {

    BorrowDetailResponse getBorrowDetail(String id);

    List<BorrowDetailResponse> getBorrowDetails();

    PageResponse<BorrowDetailResponse> getBorrowDetailsPagination(
            BorrowDetailFilter filter,
            int page,
            int size
    );

    BorrowDetailResponse returnBook(String id, ReturnBookRequest request);
}