package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.BorrowDetailFilter;
import com.example.library.dto.book.request.ReturnBookRequest;
import com.example.library.dto.book.response.BorrowDetailResponse;
import com.example.library.dto.book.response.BorrowDetailSummaryResponse;
import com.example.library.service.BorrowDetailService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-details")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowDetailController {

    BorrowDetailService borrowDetailService;

    @GetMapping("/{id}")
    public ApiResponse<BorrowDetailResponse> getBorrowDetail(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                borrowDetailService.getBorrowDetail(id)
        );
    }

    @GetMapping
    public ApiResponse<List<BorrowDetailResponse>> getBorrowDetails() {
        return ApiResponse.success(borrowDetailService.getBorrowDetails());
    }

    @GetMapping("/pagination")
    public ApiResponse<PageResponse<BorrowDetailResponse>> getPagination(
            @ModelAttribute BorrowDetailFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<BorrowDetailResponse>>builder()
                .data(borrowDetailService.getBorrowDetailsPagination(filter, page, size))
                .build();
    }

    @GetMapping("/pagination/me")
    public ApiResponse<PageResponse<BorrowDetailResponse>> getMyPagination(
            @ModelAttribute BorrowDetailFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<BorrowDetailResponse>>builder()
                .data(
                        borrowDetailService.getMyBorrowDetailsPagination(
                                filter,
                                page,
                                size
                        )
                )
                .build();
    }

    @GetMapping("/summary/me")
    public ApiResponse<BorrowDetailSummaryResponse> getMySummary() {
        return ApiResponse.success(
                borrowDetailService.getMySummary()
        );
    }

    @GetMapping("/summary/{userId}")
    @PreAuthorize("hasAuthority('BORROW_WRITE')")
    public ApiResponse<BorrowDetailSummaryResponse> getSummary(@PathVariable String userId) {
        return ApiResponse.success(
                borrowDetailService.getSummary(Long.valueOf(userId))
        );
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAuthority('BORROW_WRITE')")
    public ApiResponse<BorrowDetailResponse> returnBook(
            @PathVariable String id,
            @ModelAttribute @Valid ReturnBookRequest request
    ) {
        return ApiResponse.success(
                borrowDetailService.returnBook(id, request )
        );
    }
}