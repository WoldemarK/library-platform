package com.kroon.its.libraryservice.repository;


import com.kroon.its.libraryservice.entity.BookCopy;
import com.kroon.its.libraryservice.entity.Loan;
import com.kroon.its.libraryservice.enums.LoanStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    Page<Loan> findByUserId(String userId, Pageable pageable);

    Page<Loan> findByUserIdAndStatus(String userId, LoanStatus status, Pageable pageable);

    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);

    boolean existsByBookCopyIdAndStatus(Long bookCopyId, LoanStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(" SELECT copy FROM BookCopy copy WHERE copy.id = :id")
    Optional<BookCopy> findBookCopyForUpdate(@Param("id") Long id);
}
