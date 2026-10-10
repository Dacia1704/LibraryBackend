package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.FinePaymentRequest;
import com.example.library.dto.book.response.FinePaymentResponse;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.service.FinePaymentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fine-payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinePaymentController {

    FinePaymentService finePaymentService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINE_PAYMENT_READ')")
    public ApiResponse<PageResponse<FinePaymentResponse>> getAll(
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<FinePaymentResponse>>builder()
                .data(
                        finePaymentService.getAll(
                                memberId,
                                page,
                                size
                        )
                )
                .build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponse<FinePaymentResponse>> getMe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<FinePaymentResponse>>builder()
                .data(
                        finePaymentService.getMe(
                                page,
                                size
                        )
                )
                .build();
    }

    @GetMapping("/total/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BigDecimal> getMyTotal() {
        return ApiResponse.success(finePaymentService.getMyTotal());
    }

    @GetMapping("/total")
    @PreAuthorize("hasAuthority('FINE_PAYMENT_READ')")
    public ApiResponse<BigDecimal> getTotalByUserIdOrMemberId(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long memberId
    ) {
        if(userId==null && memberId==null) throw new AppException(ErrorCode.INVALID_REQUEST);
        if(userId != null) return ApiResponse.success(finePaymentService.getTotalByUserId(userId));
        return ApiResponse.success(finePaymentService.getTotalByMemberId(memberId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINE_PAYMENT_WRITE')")
    public ApiResponse<FinePaymentResponse> create(
            @RequestBody @Valid FinePaymentRequest request
    ) {

        return ApiResponse.<FinePaymentResponse>builder()
                .data(finePaymentService.create(request))
                .build();
    }
}