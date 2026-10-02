package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByIsDeletedFalse();
    List<Book> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByIdAndIsDeletedFalse(Long id);

}