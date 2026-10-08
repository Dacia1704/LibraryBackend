package com.example.library.entity;

import com.example.library.entity.enums.Language;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_code", nullable = false, length = 20)
    private String bookCode;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "no_accent", nullable = false, length = 255)
    private String noAccent;

    @Column(nullable = false,unique = true, length = 20)
    private String isbn;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer available = 0;

    private BigDecimal width;

    private BigDecimal height;

    private Integer pages;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String synopsis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Language language = Language.VI;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String cover;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToMany
    @JoinTable(
        name = "book_categories",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "book_publishers",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "publisher_id")
    )
    @Builder.Default
    private Set<Publisher> publishers = new HashSet<>();

    @OneToMany(mappedBy = "book")
    @Builder.Default
    private Set<BorrowDetail> borrowDetails = new HashSet<>();
}