package com.example.library.repository.specification;

import com.example.library.dto.book.request.BookFilter;
import com.example.library.entity.Book;
import com.example.library.entity.Author;
import com.example.library.entity.Category;
import com.example.library.entity.Publisher;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class BookSpecification {

    public static Specification<Book> filter(BookFilter filter) {

        return Specification
                .where(isNotDeleted())
                .and(hasKeyword(filter.getKeyword()))
                .and(hasIsbn(filter.getIsbn()))
                .and(hasMinPublishYear(filter.getMinPublishYear()))
                .and(hasMaxPublishYear(filter.getMaxPublishYear()))
                .and(hasMinQuantity(filter.getMinQuantity()))
                .and(hasMaxQuantity(filter.getMaxQuantity()))
                .and(hasMinAvailable(filter.getMinAvailable()))
                .and(hasMaxAvailable(filter.getMaxAvailable()))
                .and(hasCategories(filter.getCategoryIds()))
                .and(hasAuthors(filter.getAuthorIds()))
                .and(hasPublishers(filter.getPublisherIds()));
    }

    private static Specification<Book> isNotDeleted() {

        return (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<Book> hasKeyword(String keyword) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String value =
                    "%" + keyword.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(
                            cb.lower(root.get("title")),
                            value
                    ),
                    cb.like(
                            cb.lower(root.get("noAccent")),
                            value
                    )
            );
        };
    }

    private static Specification<Book> hasIsbn(String isbn) {

        return (root, query, cb) -> {

            if (isbn == null || isbn.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("isbn")),
                    "%" + isbn.trim().toLowerCase() + "%"
            );
        };
    }

    private static Specification<Book> hasMinPublishYear(
            Integer minPublishYear
    ) {

        return (root, query, cb) -> {

            if (minPublishYear == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                    root.get("publishYear"),
                    minPublishYear
            );
        };
    }

    private static Specification<Book> hasMaxPublishYear(
            Integer maxPublishYear
    ) {

        return (root, query, cb) -> {

            if (maxPublishYear == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                    root.get("publishYear"),
                    maxPublishYear
            );
        };
    }

    private static Specification<Book> hasMinQuantity(
            Integer minQuantity
    ) {

        return (root, query, cb) -> {

            if (minQuantity == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                    root.get("quantity"),
                    minQuantity
            );
        };
    }

    private static Specification<Book> hasMaxQuantity(
            Integer maxQuantity
    ) {

        return (root, query, cb) -> {

            if (maxQuantity == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                    root.get("quantity"),
                    maxQuantity
            );
        };
    }

    private static Specification<Book> hasMinAvailable(
            Integer minAvailable
    ) {

        return (root, query, cb) -> {

            if (minAvailable == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                    root.get("available"),
                    minAvailable
            );
        };
    }

    private static Specification<Book> hasMaxAvailable(
            Integer maxAvailable
    ) {

        return (root, query, cb) -> {

            if (maxAvailable == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                    root.get("available"),
                    maxAvailable
            );
        };
    }

    private static Specification<Book> hasCategories(
            Set<Long> categoryIds
    ) {

        return (root, query, cb) -> {

            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }

            Join<Book, Category> category =
                    root.join("categories", JoinType.INNER);

            query.distinct(true);

            return category.get("id").in(categoryIds);
        };
    }

    private static Specification<Book> hasAuthors(
            Set<Long> authorIds
    ) {

        return (root, query, cb) -> {

            if (authorIds == null || authorIds.isEmpty()) {
                return cb.conjunction();
            }

            Join<Book, Author> author =
                    root.join("authors", JoinType.INNER);

            query.distinct(true);

            return author.get("id").in(authorIds);
        };
    }

    private static Specification<Book> hasPublishers(
            Set<Long> publisherIds
    ) {

        return (root, query, cb) -> {

            if (publisherIds == null || publisherIds.isEmpty()) {
                return cb.conjunction();
            }

            Join<Book, Publisher> publisher =
                    root.join("publishers", JoinType.INNER);

            query.distinct(true);

            return publisher.get("id").in(publisherIds);
        };
    }
}