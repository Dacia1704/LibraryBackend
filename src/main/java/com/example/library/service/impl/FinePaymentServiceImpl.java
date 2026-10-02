package com.example.library.service.impl;

import com.example.library.dto.book.request.FinePaymentRequest;
import com.example.library.dto.book.response.FinePaymentResponse;
import com.example.library.entity.FinePayment;
import com.example.library.entity.Member;
import com.example.library.entity.User;
import com.example.library.mapper.FinePaymentMapper;
import com.example.library.repository.FinePaymentRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.FinePaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinePaymentServiceImpl implements FinePaymentService {

    FinePaymentRepository finePaymentRepository;
    MemberRepository memberRepository;
    UserRepository userRepository;
    FinePaymentMapper finePaymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FinePaymentResponse> getAll() {

        return finePaymentRepository.findAllByIsDeletedFalse()
                .stream()
                .map(finePaymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinePaymentResponse> getMe() {

        Long userId = getCurrentUserId();

        return finePaymentRepository.findAllByUserId(userId)
                .stream()
                .map(finePaymentMapper::toResponse)
                .toList();
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

        return finePaymentMapper.toResponse(payment);
    }

    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }
}