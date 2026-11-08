package com.kroon.its.libraryservice.entity;

import com.kroon.its.libraryservice.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "loans",
        indexes = {
                @Index(name = "idx_loans_user_id", columnList = "user_id"),
                @Index(name = "idx_loans_status", columnList = "status"),
                @Index(name = "idx_loans_due_date", columnList = "due_date")
        }
)
public class Loan extends BaseEntity{

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @Column(name = "borrowed_at", nullable = false)
    private Instant borrowedAt;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private LoanStatus status = LoanStatus.ACTIVE;

}
