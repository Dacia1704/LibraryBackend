package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.book.response.BorrowDetailResponse;
import com.example.library.service.BorrowDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
        return ApiResponse.success(
                borrowDetailService.getBorrowDetails()
        );
    }

    @PutMapping("/{id}/return")
    public ApiResponse<BorrowDetailResponse> returnBook(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                borrowDetailService.returnBook(id)
        );
    }
}