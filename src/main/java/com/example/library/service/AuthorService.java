package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.book_info.request.AuthorRequest;
import com.example.library.dto.book_info.response.AuthorResponse;

import java.util.List;

public interface AuthorService {

    AuthorResponse createAuthor(AuthorRequest request);

    AuthorResponse updateAuthor(Long id, AuthorRequest request, Boolean isRestore);

    AuthorResponse deleteAuthor(String id);

    List<AuthorResponse> getAuthors();

    PageResponse<AuthorResponse> getAuthorsPagination(String keyword, int page, int size);
}