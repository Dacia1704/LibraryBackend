package com.example.library.repository;

import com.example.library.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    Optional<Shelf> findByIdAndIsDeletedFalse(Long id);

    boolean existsByCodeAndIsDeletedFalse(String code);
}