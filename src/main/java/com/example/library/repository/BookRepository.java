package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findAllByIsDeletedFalse();
    List<Book> findAllByIdInAndIsDeletedFalse(List<Long> ids);

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