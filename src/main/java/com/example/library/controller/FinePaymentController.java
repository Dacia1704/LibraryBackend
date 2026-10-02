package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.book.request.FinePaymentRequest;
import com.example.library.dto.book.response.FinePaymentResponse;
import com.example.library.service.FinePaymentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fine-payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinePaymentController {

    FinePaymentService finePaymentService;

    @GetMapping
    public ApiResponse<List<FinePaymentResponse>> getAll() {

        return ApiResponse.<List<FinePaymentResponse>>builder()
                .data(finePaymentService.getAll())
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<List<FinePaymentResponse>> getMe() {

        return ApiResponse.<List<FinePaymentResponse>>builder()
                .data(finePaymentService.getMe())
                .build();
    }

    @PostMapping
    public ApiResponse<FinePaymentResponse> create(
            @RequestBody @Valid FinePaymentRequest request
    ) {

        return ApiResponse.<FinePaymentResponse>builder()
                .data(finePaymentService.create(request))
                .build();
    }
}