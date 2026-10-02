package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.category_author_publisher.request.AuthorRequest;
import com.example.library.dto.category_author_publisher.response.AuthorResponse;
import com.example.library.entity.Author;
import com.example.library.mapper.AuthorMapper;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.specification.AuthorSpecification;
import com.example.library.service.AuthorService;
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
public class AuthorServiceImpl implements AuthorService {

    AuthorRepository authorRepository;
    AuthorMapper authorMapper;

    @Override
    @Transactional
    @CacheEvict(value = "authors", allEntries = true)
    public AuthorResponse createAuthor(AuthorRequest request) {

        Author author = authorMapper.toAuthor(request);

        author.setNoAccent(removeAccent(request.getName()));
        author.setIsDeleted(false);

        authorRepository.save(author);

        return authorMapper.toAuthorResponse(author);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authors", allEntries = true)
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author không tồn tại"));

        author.setName(request.getName());
        author.setNoAccent(removeAccent(request.getName()));
        author.setBio(request.getBio());

        authorRepository.save(author);

        return authorMapper.toAuthorResponse(author);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authors", allEntries = true)
    public AuthorResponse deleteAuthor(String id) {

        Author author = authorRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Author không tồn tại"));

        author.setIsDeleted(true);

        authorRepository.save(author);

        return authorMapper.toAuthorResponse(author);
    }

    @Override
    @Transactional
    @Cacheable(value = "authors", key = "'all'")
    public List<AuthorResponse> getAuthors() {

        return authorRepository.findAllByIsDeletedFalse()
                .stream()
                .map(authorMapper::toAuthorResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "authors",
            key = "'page:' + #page + ':size:' + #size",
            condition = "#keyword == null || #keyword.trim().isEmpty()"
    )
    public PageResponse<AuthorResponse> getAuthorsPagination(
            String keyword,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Specification<Author> specification = Specification
                .where(AuthorSpecification.isNotDeleted())
                .and(AuthorSpecification.hasKeyword(keyword));

        Page<Author> authorPage = authorRepository.findAll(specification, pageable);

        List<AuthorResponse> content = authorPage.getContent()
                .stream()
                .map(authorMapper::toAuthorResponse)
                .toList();

        return PageResponse.<AuthorResponse>builder()
                .data(content)
                .currentPage(authorPage.getNumber())
                .pageSize(authorPage.getSize())
                .totalElements(authorPage.getTotalElements())
                .totalPages(authorPage.getTotalPages())
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