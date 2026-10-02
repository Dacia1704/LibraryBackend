package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.book.response.FineResponse;
import com.example.library.service.FineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineController {

    FineService fineService;

    @GetMapping
    public ApiResponse<List<FineResponse>> getAll() {

        return ApiResponse.<List<FineResponse>>builder()
                .data(fineService.getAll())
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<List<FineResponse>> getMe() {

        return ApiResponse.<List<FineResponse>>builder()
                .data(fineService.getMe())
                .build();
    }
}