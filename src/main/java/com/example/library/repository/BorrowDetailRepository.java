package com.example.library.repository;

import com.example.library.entity.BorrowDetail;
import com.example.library.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowDetailRepository
        extends JpaRepository<BorrowDetail, Long>,
                JpaSpecificationExecutor<BorrowDetail> {

    List<BorrowDetail> findAllByIsDeletedFalse();

    List<BorrowDetail> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    Optional<BorrowDetail> findByIdAndIsDeletedFalse(Long id);

    @Query(value = """
        SELECT
            COUNT(*) AS total,
            SUM(CASE WHEN bd.status = 'BORROWING' THEN 1 ELSE 0 END) AS borrowing,
            SUM(CASE WHEN bd.status = 'OVERDUE'   THEN 1 ELSE 0 END) AS overdue,
            SUM(CASE WHEN bd.status = 'RETURNED'  THEN 1 ELSE 0 END) AS returned
        FROM borrow_details bd
        JOIN borrow_records br ON br.id = bd.borrow_id
        JOIN members m         ON m.id = br.member_id
        JOIN users u           ON u.id = m.user_id
        WHERE u.id = :userId
          AND bd.is_deleted = 0
          AND br.is_deleted = 0
          AND m.is_deleted = 0
          AND u.is_deleted = 0
        """, nativeQuery = true)
    List<Object[]> getMyBorrowSummary(@Param("userId") Long userId);

    @Query("""
        SELECT bd
        FROM BorrowDetail bd
        JOIN FETCH bd.borrowRecord br
        WHERE br.member.id = :memberId
          AND bd.isDeleted = false
          AND br.isDeleted = false
          AND bd.status IN :statuses
          AND br.dueDate < :today
        """)
    List<BorrowDetail> findOverdueCandidatesByMember(
            @Param("memberId") Long memberId,
            @Param("today") LocalDate today,
            @Param("statuses") Collection<BorrowStatus> statuses
    );

    @Query("""
        SELECT COALESCE(SUM(bd.fineAmount), 0)
        FROM BorrowDetail bd
        JOIN bd.borrowRecord br
        WHERE br.member.id = :memberId
          AND bd.isDeleted = false
          AND br.isDeleted = false
          AND bd.status = :status
        """)
    BigDecimal sumOverdueFineAmountByMember(
            @Param("memberId") Long memberId,
            @Param("status") BorrowStatus status
    );

    @Query("""
        SELECT bd
        FROM BorrowDetail bd
        JOIN FETCH bd.borrowRecord br
        JOIN FETCH br.member m
        JOIN FETCH m.user u
        JOIN FETCH bd.book
        WHERE bd.isDeleted = false
          AND br.isDeleted = false
          AND m.isDeleted = false
          AND u.isDeleted = false
          AND bd.status = :status
        """)
    List<BorrowDetail> findAllByStatusWithDetails(
            @Param("status") BorrowStatus status
    );
}