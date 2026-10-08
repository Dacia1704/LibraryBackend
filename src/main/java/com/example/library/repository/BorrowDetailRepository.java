package com.example.library.repository;

import com.example.library.entity.BorrowDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}