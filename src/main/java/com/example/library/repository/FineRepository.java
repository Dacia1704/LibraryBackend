package com.example.library.repository;

import com.example.library.entity.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    Page<Fine> findAllByIsDeletedFalse(Pageable pageable);

    Page<Fine> findAllByBorrowRecordMemberIdAndIsDeletedFalse(
            Long memberId,
            Pageable pageable
    );

    @Query("""
        SELECT f
        FROM Fine f
        JOIN f.borrowRecord br
        JOIN br.member m
        JOIN m.user u
        WHERE u.id = :userId
          AND f.isDeleted = false
    """)
    Page<Fine> findAllByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
        SELECT COALESCE(SUM(f.amount), 0)
        FROM Fine f
        WHERE f.borrowRecord.member.id = :memberId
          AND f.isDeleted = false
    """)
    BigDecimal getTotalFineByMember(Long memberId);
}