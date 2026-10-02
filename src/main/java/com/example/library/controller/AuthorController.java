package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.category_author_publisher.request.AuthorRequest;
import com.example.library.dto.category_author_publisher.response.AuthorResponse;
import com.example.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorController {

    AuthorService authorService;

    @PostMapping
    @PreAuthorize("hasAuthority('AUTHOR_MANAGE')")
    public ApiResponse<AuthorResponse> createAuthor(
            @RequestBody @Valid AuthorRequest request
    ) {
        return ApiResponse.success(
                authorService.createAuthor(request)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('AUTHOR_MANAGE')")
    public ApiResponse<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @RequestBody @Valid AuthorRequest request
    ) {
        return ApiResponse.success(
                authorService.updateAuthor(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('AUTHOR_MANAGE')")
    public ApiResponse<AuthorResponse> deleteAuthor(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                authorService.deleteAuthor(id)
        );
    }

    @GetMapping("/all")
    public ApiResponse<List<AuthorResponse>> getAuthors() {
        return ApiResponse.success(
                authorService.getAuthors()
        );
    }

    @GetMapping("/")
    public ApiResponse<PageResponse<AuthorResponse>> getAuthorsPagination(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<AuthorResponse>>builder()
                .data(
                        authorService.getAuthorsPagination(
                                keyword,
                                page,
                                size
                        )
                )
                .build();
    }
}