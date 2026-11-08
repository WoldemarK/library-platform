package com.kroon.its.libraryservice.repository;


import com.kroon.its.libraryservice.entity.Loan;
import com.kroon.its.libraryservice.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserIdOrderByBorrowedAtDesc(String userId);

    List<Loan> findByUserIdAndStatus(String userId, LoanStatus status);

    Optional<Loan> findByIdAndUserId(Long id, String userId);

    boolean existsByBookCopyIdAndStatus(Long bookCopyId, LoanStatus status);

    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, java.time.Instant date);
}
