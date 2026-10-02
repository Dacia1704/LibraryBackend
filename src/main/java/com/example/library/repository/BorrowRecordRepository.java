package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long>, JpaSpecificationExecutor<BorrowRecord> {

    List<BorrowRecord> findAllByIsDeletedFalse();

    List<BorrowRecord> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    Optional<BorrowRecord> findByIdAndIsDeletedFalse(Long id);
}