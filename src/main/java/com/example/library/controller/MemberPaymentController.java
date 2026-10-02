package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.user.response.MemberPaymentResponse;
import com.example.library.service.MemberPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-payments")
@RequiredArgsConstructor
public class MemberPaymentController {

    private final MemberPaymentService memberPaymentService;

    @GetMapping("/{memberId}")
    @PreAuthorize("hasAuthority('MEMBER_PAYMENT_READ')")
    public ApiResponse<PageResponse<MemberPaymentResponse>> getAll(
            @PathVariable Long memberId,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<MemberPaymentResponse>>builder()
                .data(
                        memberPaymentService.getAll(
                                memberId,
                                month,
                                page,
                                size
                        )
                )
                .build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('MEMBER_PAYMENT_READ')")
    public ApiResponse<List<MemberPaymentResponse>> getMe() {

        return ApiResponse.<List<MemberPaymentResponse>>builder()
                .data(
                        memberPaymentService.getMe()
                )
                .build();
    }
}