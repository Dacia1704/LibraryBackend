package com.example.library.repository;

import com.example.library.entity.FinePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinePaymentRepository extends JpaRepository<FinePayment, Long> {

    List<FinePayment> findAllByIsDeletedFalse();

    List<FinePayment> findAllByMemberIdAndIsDeletedFalse(Long memberId);

    @Query("""
        SELECT fp
        FROM FinePayment fp
        JOIN fp.member m
        JOIN m.user u
        WHERE u.id = :userId
          AND fp.isDeleted = false
    """)
    List<FinePayment> findAllByUserId(@Param("userId") Long userId);
}