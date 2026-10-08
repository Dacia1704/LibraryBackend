package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.user.response.MemberPaymentResponse;

import java.util.List;

public interface MemberPaymentService {

    PageResponse<MemberPaymentResponse> getAll(
            Long memberId,
            Integer month,
            int page,
            int size
    );

    PageResponse<MemberPaymentResponse> getMe(Integer month, int page, int size);
}