package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.user.response.MemberPaymentResponse;
import com.example.library.entity.Member;
import com.example.library.entity.MemberPayment;
import com.example.library.mapper.MemberPaymentMapper;
import com.example.library.repository.MemberPaymentRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.MemberPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPaymentServiceImpl implements MemberPaymentService {

    private final MemberPaymentRepository memberPaymentRepository;
    private final MemberRepository memberRepository;
    private final MemberPaymentMapper memberPaymentMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberPaymentResponse> getAll(
            Long memberId,
            Integer month,
            int page,
            int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        if (month != null && (month < 1 || month > 12)) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paidAt"));

        Page<MemberPayment> paymentPage = memberPaymentRepository.findAllByMemberId(memberId, month, pageable);

        List<MemberPaymentResponse> content =
                paymentPage.getContent()
                        .stream()
                        .map(memberPaymentMapper::toResponse)
                        .toList();

        return PageResponse.<MemberPaymentResponse>builder()
                .data(content)
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .build();
    }

    @Override
    public PageResponse<MemberPaymentResponse> getMe(Integer month, int page, int size) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        if (month != null && (month < 1 || month > 12)) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<MemberPayment> paymentPage = memberPaymentRepository.findAllByUserId(userId, month, pageable);

        List<MemberPaymentResponse> content =
                paymentPage.getContent()
                        .stream()
                        .map(memberPaymentMapper::toResponse)
                        .toList();

        return PageResponse.<MemberPaymentResponse>builder()
                .data(content)
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .build();
    }
}