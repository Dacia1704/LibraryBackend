package com.example.library.service;


import com.example.library.dto.book.request.FinePaymentRequest;
import com.example.library.dto.book.response.FinePaymentResponse;

import java.util.List;

public interface FinePaymentService {

    List<FinePaymentResponse> getAll();

    List<FinePaymentResponse> getMe();

    FinePaymentResponse create(FinePaymentRequest request);
}