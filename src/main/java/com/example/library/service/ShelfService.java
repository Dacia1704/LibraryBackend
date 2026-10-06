package com.example.library.service;


import com.example.library.dto.book_info.request.ShelfRequest;
import com.example.library.dto.book_info.response.ShelfResponse;

import java.util.List;

public interface ShelfService {

    ShelfResponse create(ShelfRequest request);

    ShelfResponse getById(Long id);

    List<ShelfResponse> getAll();

    ShelfResponse update(Long id, ShelfRequest request);

    void delete(Long id);
}