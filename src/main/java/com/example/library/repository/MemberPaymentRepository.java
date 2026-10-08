package com.example.library.repository;

import com.example.library.entity.MemberPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberPaymentRepository
        extends JpaRepository<MemberPayment, Long> {

    @Query("""
        SELECT mp
        FROM MemberPayment mp
        WHERE mp.member.id = :memberId
          AND mp.isDeleted = false
          AND (:month IS NULL OR MONTH(mp.paidAt) = :month)
    """)
    Page<MemberPayment> findAllByMemberId(
            @Param("memberId") Long memberId,
            @Param("month") Integer month,
            Pageable pageable
    );

    @Query("""
        SELECT mp
        FROM MemberPayment mp
        JOIN mp.member m
        JOIN m.user u
        WHERE u.id = :userId
          AND mp.isDeleted = false
        ORDER BY mp.paidAt DESC
    """)
    List<MemberPayment> findAllByUserId(
            @Param("userId") Long userId
    );

    @Query("""
        SELECT mp
        FROM MemberPayment mp
        JOIN mp.member m
        JOIN m.user u
        WHERE u.id = :userId
          AND mp.isDeleted = false
          AND (:month IS NULL OR MONTH(mp.paidAt) = :month)
        ORDER BY mp.paidAt DESC
    """)
    Page<MemberPayment> findAllByUserId(
            @Param("userId") Long userId,
            @Param("month") Integer month,
            Pageable pageable
    );
}