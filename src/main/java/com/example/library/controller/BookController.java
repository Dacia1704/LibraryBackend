package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.BookFilter;
import com.example.library.dto.book.request.BookRequest;
import com.example.library.dto.book.response.BookResponse;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    BookService bookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BookResponse> createBook(
            @ModelAttribute @Valid BookRequest request
    ) {
        return ApiResponse.success(
                bookService.createBook(request)
        );
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<BookResponse> updateBook(
            @PathVariable Long id,
            @ModelAttribute @Valid BookRequest request
    ) {
        return ApiResponse.success(
                bookService.updateBook(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<BookResponse> deleteBook(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                bookService.deleteBook(id)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<BookResponse> getBook(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                bookService.getBook(id)
        );
    }

    @GetMapping
    public ApiResponse<List<BookResponse>> getBooks() {
        return ApiResponse.success(
                bookService.getBooks()
        );
    }

    @GetMapping("/deleted/{id}")
    public ApiResponse<BookResponse> getBookDeleted(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                bookService.getBookDeleted(id)
        );
    }

    @GetMapping("/")
    public ApiResponse<PageResponse<BookResponse>> getBooksPagination(
            @ModelAttribute BookFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<BookResponse>>builder()
                .data(
                        bookService.getBooksPagination(
                                filter,
                                page,
                                size
                        )
                )
                .build();
    }
}
