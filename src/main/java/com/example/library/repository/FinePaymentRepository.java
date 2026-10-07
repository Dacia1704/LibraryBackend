package com.example.library.repository;

import com.example.library.entity.FinePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FinePaymentRepository extends JpaRepository<FinePayment, Long> {

    Page<FinePayment> findAllByIsDeletedFalse(Pageable pageable);

    Page<FinePayment> findAllByMemberIdAndIsDeletedFalse(
            Long memberId,
            Pageable pageable
    );

    @Query("""
        SELECT fp
        FROM FinePayment fp
        JOIN fp.member m
        JOIN m.user u
        WHERE u.id = :userId
          AND fp.isDeleted = false
    """)
    Page<FinePayment> findAllByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
        SELECT COALESCE(SUM(fp.amount), 0)
        FROM FinePayment fp
        WHERE fp.member.id = :memberId
          AND fp.isDeleted = false
    """)
    BigDecimal getTotalPaymentByMember(Long memberId);

    @Query("""
        SELECT COALESCE(SUM(fp.amount), 0)
        FROM FinePayment fp
        JOIN fp.member m
        JOIN m.user u
        WHERE u.id = :userId
          AND fp.isDeleted = false
    """)
    BigDecimal getTotalPaymentByUserId(@Param("userId") Long userId);
}