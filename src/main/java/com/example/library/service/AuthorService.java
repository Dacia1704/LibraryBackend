package com.example.library.service;


import com.example.library.dto.category_author_publisher.request.AuthorRequest;
import com.example.library.dto.category_author_publisher.response.AuthorResponse;

import java.util.List;

public interface AuthorService {

    AuthorResponse createAuthor(AuthorRequest request);

    AuthorResponse updateAuthor(Long id, AuthorRequest request);

    AuthorResponse deleteAuthor(String id);

    List<AuthorResponse> getAuthors();
}