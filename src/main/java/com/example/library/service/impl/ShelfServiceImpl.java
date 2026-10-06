package com.example.library.service.impl;

import com.example.library.dto.book_info.request.ShelfRequest;
import com.example.library.dto.book_info.response.ShelfResponse;
import com.example.library.entity.Shelf;
import com.example.library.mapper.ShelfMapper;
import com.example.library.repository.ShelfRepository;
import com.example.library.service.ShelfService;
import com.example.library.utils.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShelfServiceImpl implements ShelfService {

    private final ShelfRepository shelfRepository;
    private final ShelfMapper shelfMapper;

    @Override
    public ShelfResponse create(ShelfRequest request) {

        Shelf shelf = shelfMapper.toEntity(request);

        shelf.setCode(generateShelfCode());
        shelf.setIsDeleted(false);

        shelf = shelfRepository.save(shelf);

        return shelfMapper.toResponse(shelf);
    }

    @Override
    @Transactional(readOnly = true)
    public ShelfResponse getById(Long id) {

        Shelf shelf = shelfRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));

        return shelfMapper.toResponse(shelf);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShelfResponse> getAll() {

        return shelfRepository.findAll()
                .stream()
                .filter(shelf -> !shelf.getIsDeleted())
                .map(shelfMapper::toResponse)
                .toList();
    }

    @Override
    public ShelfResponse update(Long id, ShelfRequest request) {

        Shelf shelf = shelfRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));

        shelfMapper.updateEntity(request, shelf);

        shelf = shelfRepository.save(shelf);

        return shelfMapper.toResponse(shelf);
    }

    @Override
    public void delete(Long id) {

        Shelf shelf = shelfRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));

        shelf.setIsDeleted(true);

        shelfRepository.save(shelf);
    }

    private String generateShelfCode() {
        return TextUtils.generateCode("SH");
    }
}