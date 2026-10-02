package com.example.library.repository;

import com.example.library.entity.BorrowDetail;
import com.example.library.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowDetailRepository extends JpaRepository<BorrowDetail, Long> {

    List<BorrowDetail> findAllByIsDeletedFalse();

    List<BorrowDetail> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    Optional<BorrowDetail> findByIdAndIsDeletedFalse(Long id);
}