package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.category_author_publisher.request.AuthorRequest;
import com.example.library.dto.category_author_publisher.response.AuthorResponse;
import com.example.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorController {

    AuthorService authorService;

    @PostMapping
    public ApiResponse<AuthorResponse> createAuthor(
            @RequestBody @Valid AuthorRequest request
    ) {
        return ApiResponse.success(
                authorService.createAuthor(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @RequestBody @Valid AuthorRequest request
    ) {
        return ApiResponse.success(
                authorService.updateAuthor(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<AuthorResponse> deleteAuthor(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                authorService.deleteAuthor(id)
        );
    }

    @GetMapping
    public ApiResponse<List<AuthorResponse>> getAuthors() {
        return ApiResponse.success(
                authorService.getAuthors()
        );
    }
}