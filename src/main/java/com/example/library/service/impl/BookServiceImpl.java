package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.BookFilter;
import com.example.library.dto.book.request.BookRequest;
import com.example.library.dto.book.response.BookResponse;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.entity.Publisher;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.CategoryRepository;
import com.example.library.repository.PublisherRepository;
import com.example.library.repository.specification.BookSpecification;
import com.example.library.service.BookService;
import com.example.library.utils.TextUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookServiceImpl implements BookService {

    BookRepository bookRepository;
    CategoryRepository categoryRepository;
    AuthorRepository authorRepository;
    PublisherRepository publisherRepository;

    BookMapper bookMapper;

    @Override
    public BookResponse createBook(BookRequest request) {
        Optional<Book> exist = bookRepository.findByIsbn(request.getIsbn());
        if (exist.isPresent()) throw new AppException(ErrorCode.BOOK_EXISTED, String.format("Sách với mã ISBN %s đã tồn tại", request.getIsbn()));

        Book book = bookMapper.toBook(request);
        book.setNoAccent(TextUtils.removeAccent(book.getTitle()));
        book.setPrice(request.getPrice());

        if(request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories =  categoryRepository.findAllByIdInAndIsDeletedFalse(request.getCategoryIds().stream().toList());
            if (categories.size() != request.getCategoryIds().size()) throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            book.setCategories(new HashSet<>(categories));
        }

        if(request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            List<Author> authors = authorRepository.findAllByIdInAndIsDeletedFalse(request.getAuthorIds().stream().toList());
            if (authors.size() != request.getAuthorIds().size()) throw new AppException(ErrorCode.AUTHOR_NOT_FOUND);
            book.setAuthors(new HashSet<>(authors));
        }

        if(request.getPublisherIds() != null && !request.getPublisherIds().isEmpty()) {
            List<Publisher> publishers = publisherRepository.findAllByIdInAndIsDeletedFalse(request.getPublisherIds().stream().toList());
            if (publishers.size() != request.getPublisherIds().size()) throw new AppException(ErrorCode.PUBLISHER_NOT_FOUND);
            book.setPublishers(new HashSet<>(publishers));
        }

        if (request.getCover() != null && !request.getCover().isEmpty()) {
            try {
                book.setCover(Base64.getEncoder().encodeToString(request.getCover().getBytes()));
            } catch (IOException e) {
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }
        bookRepository.save(book);

        return null;
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest request, Boolean isRestore) {
        // 1. Tìm Book
        Book book = bookRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        // 2. Kiểm tra ISBN
        Optional<Book> exist = bookRepository.findByIsbn(request.getIsbn());

        if (exist.isPresent() && !exist.get().getId().equals(id)) {
            throw new AppException(
                    ErrorCode.BOOK_EXISTED,
                    String.format("Sách với mã ISBN %s đã tồn tại", request.getIsbn())
            );
        }

        // 3. Cập nhật thông tin cơ bản
        bookMapper.updateBook(book, request);
        book.setNoAccent(TextUtils.removeAccent(request.getTitle()));
        book.setPrice(request.getPrice());

        if(request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories =  categoryRepository.findAllByIdInAndIsDeletedFalse(request.getCategoryIds().stream().toList());
            if (categories.size() != request.getCategoryIds().size()) throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            book.setCategories(new HashSet<>(categories));
        }

        if(request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            List<Author> authors = authorRepository.findAllByIdInAndIsDeletedFalse(request.getAuthorIds().stream().toList());
            if (authors.size() != request.getAuthorIds().size()) throw new AppException(ErrorCode.AUTHOR_NOT_FOUND);
            book.setAuthors(new HashSet<>(authors));
        }

        if(request.getPublisherIds() != null && !request.getPublisherIds().isEmpty()) {
            List<Publisher> publishers = publisherRepository.findAllByIdInAndIsDeletedFalse(request.getPublisherIds().stream().toList());
            if (publishers.size() != request.getPublisherIds().size()) throw new AppException(ErrorCode.PUBLISHER_NOT_FOUND);
            book.setPublishers(new HashSet<>(publishers));
        }

        if (request.getCover() != null && !request.getCover().isEmpty()) {
            try {
                book.setCover(Base64.getEncoder().encodeToString(request.getCover().getBytes()));
            } catch (IOException e) {
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        if(isRestore) book.setIsDeleted(false);

        // 8. Save
        bookRepository.save(book);

        return bookMapper.toBookResponse(book);
    }

    @Override
    @Transactional
    public BookResponse deleteBook(String id) {

        Book book = bookRepository.findById(Long.valueOf(id))
                .orElseThrow(() ->
                        new AppException(ErrorCode.BOOK_NOT_FOUND)
                );
        book.setIsDeleted(true);

        bookRepository.save(book);

        return bookMapper.toBookResponse(book);
    }
    @Override
    @Transactional(readOnly = true)
    public BookResponse getBook(String id) {
        Book book = bookRepository
                .findByIdAndIsDeletedFalse(Long.valueOf(id))
                .orElseThrow(() ->
                        new AppException(ErrorCode.BOOK_NOT_FOUND)
                );
        return bookMapper.toBookResponse(book);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getBooks() {
        return bookRepository.findAllByIsDeletedFalse()
                .stream()
                .map(bookMapper::toBookResponse)
                .toList();
    }

    @Override
    public BookResponse getBookDeleted(String id) {
        Book book = bookRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return bookMapper.toBookResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooksPagination(
            BookFilter filter,
            int page,
            int size
    ) {
        if (page < 0) page = 0;

        if (size <= 0) size = 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Specification<Book> specification = BookSpecification.filter(filter);

        Page<Book> bookPage = bookRepository.findAll(specification, pageable);

        List<BookResponse> content =
                bookPage.getContent().stream().map(bookMapper::toBookResponse).toList();

        return PageResponse.<BookResponse>builder()
                .data(content)
                .currentPage(bookPage.getNumber())
                .pageSize(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .build();
    }


}