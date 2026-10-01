package com.example.library.service.impl;

import com.example.library.dto.category_author_publisher.request.AuthorRequest;
import com.example.library.dto.category_author_publisher.response.AuthorResponse;
import com.example.library.entity.Author;
import com.example.library.mapper.AuthorMapper;
import com.example.library.repository.AuthorRepository;
import com.example.library.service.AuthorService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

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
    public AuthorResponse createAuthor(AuthorRequest request) {

        Author author = authorMapper.toAuthor(request);

        author.setNoAccent(removeAccent(request.getName()));
        author.setIsDeleted(false);

        authorRepository.save(author);

        return authorMapper.toAuthorResponse(author);
    }

    @Override
    @Transactional
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
    public AuthorResponse deleteAuthor(String id) {

        Author author = authorRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Author không tồn tại"));

        author.setIsDeleted(true);

        authorRepository.save(author);

        return authorMapper.toAuthorResponse(author);
    }

    @Override
    @Transactional
    public List<AuthorResponse> getAuthors() {

        return authorRepository.findAllByIsDeletedFalse()
                .stream()
                .map(authorMapper::toAuthorResponse)
                .toList();
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