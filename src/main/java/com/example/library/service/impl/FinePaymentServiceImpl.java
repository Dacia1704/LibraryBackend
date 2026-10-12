package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.FinePaymentRequest;
import com.example.library.dto.book.response.FinePaymentResponse;
import com.example.library.entity.FinePayment;
import com.example.library.entity.Member;
import com.example.library.entity.User;
import com.example.library.mapper.FinePaymentMapper;
import com.example.library.messaging.NotificationProducer;
import com.example.library.repository.FinePaymentRepository;
import com.example.library.repository.FineRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.FinePaymentService;
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
public class FinePaymentServiceImpl implements FinePaymentService {

    FinePaymentRepository finePaymentRepository;
    MemberRepository memberRepository;
    UserRepository userRepository;
    FinePaymentMapper finePaymentMapper;
    NotificationProducer notificationProducer;
    FineRepository fineRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinePaymentResponse> getAll(
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

        Page<FinePayment> paymentPage;

        if (memberId != null) {
            paymentPage = finePaymentRepository
                    .findAllByMemberIdAndIsDeletedFalse(
                            memberId,
                            pageable
                    );
        } else {
            paymentPage = finePaymentRepository
                    .findAllByIsDeletedFalse(pageable);
        }

        List<FinePaymentResponse> content = paymentPage.getContent()
                .stream()
                .map(finePaymentMapper::toResponse)
                .toList();

        return PageResponse.<FinePaymentResponse>builder()
                .data(content)
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinePaymentResponse> getMe(
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

        Page<FinePayment> paymentPage =
                finePaymentRepository.findAllByUserId(
                        userId,
                        pageable
                );

        List<FinePaymentResponse> content = paymentPage.getContent()
                .stream()
                .map(finePaymentMapper::toResponse)
                .toList();

        return PageResponse.<FinePaymentResponse>builder()
                .data(content)
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public FinePaymentResponse create(FinePaymentRequest request) {

        Long userId = getCurrentUserId();

        Member member = memberRepository
                .findByIdAndIsDeletedFalse(request.getMemberId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Member not found"));

        User receivedBy = userRepository
                .findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        FinePayment payment = FinePayment.builder()
                .member(member)
                .amount(request.getAmount())
                .receivedBy(receivedBy)
                .note(request.getNote())
                .isDeleted(false)
                .build();

        payment = finePaymentRepository.save(payment);

        // Gửi thông báo cho người trả phạt
        BigDecimal remainingFine = fineRepository.getTotalFineByMember(member.getId());
        if (remainingFine.compareTo(BigDecimal.ZERO) > 0) {
            notificationProducer.send(member.getUser().getId(),
                    String.format("Bạn đã trả %,.0f VNĐ tiền phạt thành công. Hiện bạn còn %,.0f VNĐ tiền phạt.", 
                            request.getAmount(), remainingFine));
        } else {
            notificationProducer.send(member.getUser().getId(),
                    String.format("Bạn đã thanh toán toàn bộ tiền phạt (%,.0f VNĐ). Cố giữ vững quy định của thư viện nhé!", 
                            request.getAmount()));
        }

        return finePaymentMapper.toResponse(payment);
    }

    @Override
    public BigDecimal getTotalByUserId(Long userId) {
        return finePaymentRepository.getTotalPaymentByUserId(userId);
    }

    @Override
    public BigDecimal getTotalByMemberId(Long memberId) {
        return finePaymentRepository.getTotalPaymentByMember(memberId);
    }

    @Override
    public BigDecimal getMyTotal() {

        return getTotalByUserId(getCurrentUserId());
    }

    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }
}