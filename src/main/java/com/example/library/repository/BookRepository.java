package com.example.library.repository;

import com.example.library.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findAllByIsDeletedFalse();
    List<Book> findAllByIdInAndIsDeletedFalse(List<Long> ids);

    /**
     * Tìm tất cả sách theo IDs với PESSIMISTIC_WRITE lock
     * Để tránh race condition khi nhiều người cùng mượn sách cuối cùng
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id IN :ids AND b.isDeleted = false")
    List<Book> findAllByIdInWithLock(@Param("ids") List<Long> ids);

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByIdAndIsDeletedFalse(Long id);

    /**
     * Đếm tổng số đầu sách (chưa xóa)
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.isDeleted = false")
    Long countTotalTitles();

    /**
     * Đếm tổng số lượng bản ghi (quantity) của tất cả sách
     */
    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM Book b WHERE b.isDeleted = false")
    Long sumTotalCopies();

    /**
     * Đếm số sách đang mượn (quantity - available) của tất cả sách
     */
    @Query("SELECT COALESCE(SUM(b.quantity - b.available), 0) FROM Book b WHERE b.isDeleted = false")
    Long sumBorrowedCopies();

    /**
     * Đếm số đầu sách đã hết (available = 0)
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.isDeleted = false AND b.available = 0")
    Long countOutOfStockTitles();
}