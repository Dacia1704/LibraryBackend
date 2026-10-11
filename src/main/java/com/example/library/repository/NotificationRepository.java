package com.example.library.repository;

import com.example.library.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    List<Notification> findByMemberIdAndIsDeletedFalse(Long memberId);

    List<Notification> findByMemberIdAndIsReadAndIsDeletedFalse(Long memberId, Boolean isRead);

    Optional<Notification> findByIdAndIsDeletedFalse(Long id);
}