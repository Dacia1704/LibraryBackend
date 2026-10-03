package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.category_author_publisher.request.PublisherRequest;
import com.example.library.dto.category_author_publisher.response.PublisherResponse;
import com.example.library.service.PublisherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublisherController {

    PublisherService publisherService;

    @PostMapping
    @PreAuthorize("hasAuthority('PUBLISHER_MANAGE')")
    public ApiResponse<PublisherResponse> createPublisher(@RequestBody @Valid PublisherRequest request) {
        return ApiResponse.success(publisherService.createPublisher(request));
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('PUBLISHER_MANAGE')")
    public ApiResponse<PublisherResponse> restorePublisher(@PathVariable Long id, @RequestBody @Valid PublisherRequest request) {
        return ApiResponse.success(publisherService.updatePublisher(id, request, true));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PUBLISHER_MANAGE')")
    public ApiResponse<PublisherResponse> updatePublisher(@PathVariable Long id, @RequestBody @Valid PublisherRequest request) {
        return ApiResponse.success(publisherService.updatePublisher(id, request, false));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PUBLISHER_MANAGE')")
    public ApiResponse<PublisherResponse> deletePublisher(@PathVariable String id) {
        return ApiResponse.success(publisherService.deletePublisher(id));
    }

    @GetMapping("/all")
    public ApiResponse<List<PublisherResponse>> getPublishers(
            @RequestParam(required = false) String keyword
    ) {

        return ApiResponse.<List<PublisherResponse>>builder()
                .data(
                        publisherService.getPublishers(
                                keyword
                        )
                )
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<PublisherResponse>> getPublishers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<PublisherResponse>>builder()
                .data(
                        publisherService.getPublishersPagination(
                                keyword,
                                page,
                                size
                        )
                )
                .build();
    }
}