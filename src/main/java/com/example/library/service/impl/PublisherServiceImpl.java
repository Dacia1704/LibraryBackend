package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.book_info.request.PublisherRequest;
import com.example.library.dto.book_info.response.PublisherResponse;
import com.example.library.entity.Publisher;
import com.example.library.mapper.PublisherMapper;
import com.example.library.repository.PublisherRepository;
import com.example.library.repository.specification.PublisherSpecification;
import com.example.library.service.PublisherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublisherServiceImpl implements PublisherService {

    PublisherRepository publisherRepository;
    PublisherMapper publisherMapper;

    @Override
    @Transactional
    @CacheEvict(value = "publishers", allEntries = true)
    public PublisherResponse createPublisher(PublisherRequest request) {

        Publisher publisher = publisherMapper.toPublisher(request);

        publisher.setNoAccent(removeAccent(request.getName()));
        publisher.setIsDeleted(false);

        publisherRepository.save(publisher);

        return publisherMapper.toPublisherResponse(publisher);
    }

    @Override
    @Transactional
    @CacheEvict(value = "publishers", allEntries = true)
    public PublisherResponse updatePublisher(Long id, PublisherRequest request, Boolean isRestore) {

        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher không tồn tại"));

        publisher.setName(request.getName());
        publisher.setNoAccent(removeAccent(request.getName()));
        publisher.setAddress(request.getAddress());

        if(isRestore) publisher.setIsDeleted(false);

        publisherRepository.save(publisher);

        return publisherMapper.toPublisherResponse(publisher);
    }

    @Override
    @Transactional
    @CacheEvict(value = "publishers", allEntries = true)
    public PublisherResponse deletePublisher(String id) {

        Publisher publisher = publisherRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Publisher không tồn tại"));

        publisher.setIsDeleted(true);

        publisherRepository.save(publisher);

        return publisherMapper.toPublisherResponse(publisher);
    }

    @Override
    @Transactional
    @Cacheable(value = "publishers", key = "'all'")
    public List<PublisherResponse> getPublishers(String keyword) {

        Specification<Publisher> specification =
                PublisherSpecification.filter(keyword);

        List<Publisher> publisherPage =
                publisherRepository.findAll(specification);

        return publisherPage.stream()
                        .map(publisherMapper::toPublisherResponse)
                        .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "publishers",
            key = "'page:' + #page + ':size:' + #size",
            condition = "#keyword == null || #keyword.trim().isEmpty()"
    )
    public PageResponse<PublisherResponse> getPublishersPagination(
            String keyword,
            int page,
            int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Specification<Publisher> specification =
                PublisherSpecification.filter(keyword);

        Page<Publisher> publisherPage =
                publisherRepository.findAll(specification, pageable);

        List<PublisherResponse> content =
                publisherPage.getContent()
                        .stream()
                        .map(publisherMapper::toPublisherResponse)
                        .toList();

        return PageResponse.<PublisherResponse>builder()
                .data(content)
                .currentPage(publisherPage.getNumber())
                .pageSize(publisherPage.getSize())
                .totalElements(publisherPage.getTotalElements())
                .totalPages(publisherPage.getTotalPages())
                .build();
    }

    private String removeAccent(String value) {

        if (value == null) {
            return null;
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase()
                .trim();
    }
}