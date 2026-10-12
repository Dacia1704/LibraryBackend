package com.example.library.repository.specification;

import com.example.library.dto.notification.request.NotificationFilter;
import com.example.library.entity.Notification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class NotificationSpecification {

    public static Specification<Notification> filter(NotificationFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Không lấy các record đã xóa
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            if (filter == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Lọc theo userId
            if (filter.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), filter.getUserId()));
            }

            // Lọc theo isRead
            if (filter.getIsRead() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isRead"), filter.getIsRead()));
            }

            // Tìm kiếm theo keyword trong content
            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("content")),
                        "%" + filter.getKeyword().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
