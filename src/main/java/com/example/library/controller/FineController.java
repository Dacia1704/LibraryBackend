package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.book.response.FineResponse;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.service.FineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineController {

    FineService fineService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINE_READ')")
    public ApiResponse<PageResponse<FineResponse>> getAll(
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<FineResponse>>builder()
                .data(fineService.getAll(memberId, page, size))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<PageResponse<FineResponse>> getMe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<FineResponse>>builder()
                .data(fineService.getMe(page, size))
                .build();
    }

    @GetMapping("/total/me")
    public ApiResponse<BigDecimal> getMyTotal() {
        return ApiResponse.success(fineService.getMyTotal());
    }

    @GetMapping("/total")
    @PreAuthorize("hasAuthority('FINE_READ')")
    public ApiResponse<BigDecimal> getTotalByUserId(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long memberId
    ) {
        if(userId==null && memberId==null) throw new AppException(ErrorCode.INVALID_REQUEST);
        if(userId != null) return ApiResponse.success(fineService.getTotalByUserId(userId));
        return ApiResponse.success(fineService.getTotalByMember(memberId));
    }
}