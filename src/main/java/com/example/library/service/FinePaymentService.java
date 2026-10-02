package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.FinePaymentRequest;
import com.example.library.dto.book.response.FinePaymentResponse;

public interface FinePaymentService {

    PageResponse<FinePaymentResponse> getAll(
            Long memberId,
            int page,
            int size
    );

    PageResponse<FinePaymentResponse> getMe(
            int page,
            int size
    );

    FinePaymentResponse create(FinePaymentRequest request);
}