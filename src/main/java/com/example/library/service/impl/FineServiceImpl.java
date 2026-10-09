package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.book.response.FineResponse;
import com.example.library.entity.Fine;
import com.example.library.mapper.FineMapper;
import com.example.library.repository.FinePaymentRepository;
import com.example.library.repository.FineRepository;
import com.example.library.service.FineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineServiceImpl implements FineService {

    FineRepository fineRepository;
    FineMapper fineMapper;
    FinePaymentRepository finePaymentRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FineResponse> getAll(
            Long memberId,
            int page,
            int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Fine> finePage;

        if (memberId != null) {
            finePage = fineRepository
                    .findAllByBorrowRecordMemberIdAndIsDeletedFalse(
                            memberId,
                            pageable
                    );
        } else {
            finePage = fineRepository
                    .findAllByIsDeletedFalse(pageable);
        }

        List<FineResponse> content = finePage.getContent()
                .stream()
                .map(fineMapper::toResponse)
                .toList();

        return PageResponse.<FineResponse>builder()
                .data(content)
                .currentPage(finePage.getNumber())
                .pageSize(finePage.getSize())
                .totalElements(finePage.getTotalElements())
                .totalPages(finePage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FineResponse> getMe(
            int page,
            int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Long userId = Long.valueOf(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        Page<Fine> finePage =
                fineRepository.findAllByUserId(userId, pageable);

        List<FineResponse> content = finePage.getContent()
                .stream()
                .map(fineMapper::toResponse)
                .toList();

        return PageResponse.<FineResponse>builder()
                .data(content)
                .currentPage(finePage.getNumber())
                .pageSize(finePage.getSize())
                .totalElements(finePage.getTotalElements())
                .totalPages(finePage.getTotalPages())
                .build();
    }

    @Override
    public BigDecimal getTotalByMember(Long id) {

        return fineRepository.getTotalFineByMember(id);
    }

    @Override
    public BigDecimal getTotalByUserId(Long userId) {

        return fineRepository.getTotalFineByUserId(userId);
    }

    @Override
    public BigDecimal getMyTotal() {

        Long userId = Long.valueOf(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        return getTotalByUserId(userId);
    }
}