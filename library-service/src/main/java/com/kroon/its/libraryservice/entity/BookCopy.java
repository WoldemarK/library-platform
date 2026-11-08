package com.kroon.its.libraryservice.entity;

import com.kroon.its.libraryservice.enums.BookStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "book_copies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_book_copy_inventory_number",
                        columnNames = "inventory_number"
                )
        }
)
public class BookCopy extends BaseEntity{

    @Column(name = "inventory_number", nullable = false, length = 100)
    private String inventoryNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private BookStatus status = BookStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Version
    private Long version;
}
