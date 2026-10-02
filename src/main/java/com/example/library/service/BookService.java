package com.example.library.service;


import com.example.library.dto.book.request.BookRequest;
import com.example.library.dto.book.response.BookResponse;

import java.util.List;

public interface BookService {

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(Long id, BookRequest request);

    BookResponse deleteBook(String id);

    BookResponse getBook(String id);

    List<BookResponse> getBooks();

    BookResponse getBookDeleted(String id);
}