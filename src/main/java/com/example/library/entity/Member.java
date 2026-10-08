package com.example.library.entity;

import com.example.library.entity.enums.CardStatus;
import com.example.library.entity.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "member_code", nullable = false, length = 20)
    private String memberCode;

    @Column(length = 15)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(name = "identity_number", nullable = false, length = 12)
    private String identityNumber;

    @Column(name = "card_expiry", nullable = false)
    private LocalDate cardExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_status", nullable = false)
    CardStatus cardStatus;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private Set<BorrowRecord> borrowRecords = new HashSet<>();

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private Set<Notification> notifications = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}