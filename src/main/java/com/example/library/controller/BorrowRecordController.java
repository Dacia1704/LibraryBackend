package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.book.request.BorrowRecordRequest;
import com.example.library.dto.book.response.BorrowRecordResponse;
import com.example.library.service.BorrowRecordService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowRecordController {

    BorrowRecordService borrowRecordService;

    @PostMapping
    public ApiResponse<BorrowRecordResponse> createBorrowRecord(
            @RequestBody @Valid BorrowRecordRequest request
    ) {
        return ApiResponse.success(
                borrowRecordService.createBorrowRecord(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<BorrowRecordResponse> updateBorrowRecord(
            @PathVariable Long id,
            @RequestBody @Valid BorrowRecordRequest request
    ) {
        return ApiResponse.success(
                borrowRecordService.updateBorrowRecord(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<BorrowRecordResponse> deleteBorrowRecord(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                borrowRecordService.deleteBorrowRecord(id)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<BorrowRecordResponse> getBorrowRecord(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                borrowRecordService.getBorrowRecord(id)
        );
    }

    @GetMapping
    public ApiResponse<List<BorrowRecordResponse>> getBorrowRecords() {
        return ApiResponse.success(
                borrowRecordService.getBorrowRecords()
        );
    }
}