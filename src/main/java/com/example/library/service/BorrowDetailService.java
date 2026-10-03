package com.example.library.service;


import com.example.library.dto.book.request.ReturnBookRequest;
import com.example.library.dto.book.response.BorrowDetailResponse;

import java.util.List;

public interface BorrowDetailService {

    BorrowDetailResponse getBorrowDetail(String id);

    List<BorrowDetailResponse> getBorrowDetails();

    BorrowDetailResponse returnBook(String id, ReturnBookRequest request);
}