package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.book.response.FineResponse;

import java.math.BigDecimal;

public interface FineService {

    PageResponse<FineResponse> getAll(
            Long memberId,
            int page,
            int size
    );

    PageResponse<FineResponse> getMe(
            int page,
            int size
    );

    BigDecimal getTotalByMember(Long id);
}