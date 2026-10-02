package com.example.library.service.impl;

import com.example.library.dto.book.response.FineResponse;
import com.example.library.mapper.FineMapper;
import com.example.library.repository.FineRepository;
import com.example.library.service.FineService;
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
public class FineServiceImpl implements FineService {

    FineRepository fineRepository;
    FineMapper fineMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FineResponse> getAll() {

        return fineRepository.findAllByIsDeletedFalse()
                .stream()
                .map(fineMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineResponse> getMe() {

        Long userId = getCurrentUserId();

        return fineRepository.findAllByUserId(userId)
                .stream()
                .map(fineMapper::toResponse)
                .toList();
    }

    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }
}