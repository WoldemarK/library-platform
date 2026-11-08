package com.kroon.its.libraryservice.repository;

import com.kroon.its.libraryservice.entity.BookCopy;
import com.kroon.its.libraryservice.enums.BookStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    Optional<BookCopy> findByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumber(String inventoryNumber);

    List<BookCopy> findByBookId(Long bookId);

    long countByBookId(Long bookId);

    long countByBookIdAndStatus(Long bookId, BookStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT copy FROM BookCopy copy WHERE copy.id = :id")
    Optional<BookCopy> findByIdForUpdate(@Param("id") Long id);
}
