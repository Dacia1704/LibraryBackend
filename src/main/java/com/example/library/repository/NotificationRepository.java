package com.example.library.repository;

import com.example.library.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    List<Notification> findByUserIdAndIsDeletedFalse(Long userId);

    List<Notification> findByUserIdAndIsReadAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Boolean isRead);
    List<Notification> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndIsReadAndIsDeletedFalse(Long userId, Boolean isRead);

    Optional<Notification> findByIdAndIsDeletedFalse(Long id);
}