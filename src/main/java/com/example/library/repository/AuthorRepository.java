package com.example.library.repository;

import com.example.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {

    List<Author> findAllByIsDeletedFalse();
    List<Author> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    Optional<Author> findByIdAndIsDeletedFalse(Long id);
    boolean existsByNameAndIsDeletedFalse(String name);

    boolean existsByNameAndIdNotAndIsDeletedFalse(
            String name,
            Long id
    );
}