package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.category_author_publisher.request.PublisherRequest;
import com.example.library.dto.category_author_publisher.response.PublisherResponse;
import com.example.library.service.PublisherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublisherController {

    PublisherService publisherService;

    @PostMapping
    public ApiResponse<PublisherResponse> createPublisher(@RequestBody @Valid PublisherRequest request) {
        return ApiResponse.success(publisherService.createPublisher(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PublisherResponse> updatePublisher(@PathVariable Long id, @RequestBody @Valid PublisherRequest request) {
        return ApiResponse.success(publisherService.updatePublisher(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<PublisherResponse> deletePublisher(@PathVariable String id) {
        return ApiResponse.success(publisherService.deletePublisher(id));
    }

    @GetMapping
    public ApiResponse<List<PublisherResponse>> getPublishers() {
        return ApiResponse.success(publisherService.getPublishers());
    }
}