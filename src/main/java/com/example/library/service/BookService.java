package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.BookFilter;
import com.example.library.dto.book.request.BookRequest;
import com.example.library.dto.book.response.BookResponse;
import com.example.library.dto.book.response.BookStatisticsResponse;

import java.util.List;

public interface BookService {

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(Long id, BookRequest request, Boolean isRestore);

    BookResponse deleteBook(String id);

    BookResponse getBook(String id);

    List<BookResponse> getBooks(String keyword);

    BookResponse getBookDeleted(String id);

    PageResponse<BookResponse> getBooksPagination(
            BookFilter filter,
            int page,
            int size
    );

    BookStatisticsResponse getStatistics();
}