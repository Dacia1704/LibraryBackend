package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.category_author_publisher.request.PublisherRequest;
import com.example.library.dto.category_author_publisher.response.PublisherResponse;

import java.util.List;

public interface PublisherService {

    PublisherResponse createPublisher(PublisherRequest request);

    PublisherResponse updatePublisher(Long id, PublisherRequest request);

    PublisherResponse deletePublisher(String id);

    PageResponse<PublisherResponse> getPublishers(
            String keyword,
            int page,
            int size
    );
}