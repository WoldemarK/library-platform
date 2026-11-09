package com.kroon.its.libraryservice.service;

import com.kroon.its.libraryservice.dto.request.LoanCreateRequest;
import com.kroon.its.libraryservice.dto.response.LoanResponse;
import com.kroon.its.libraryservice.dto.response.PageResponse;
import com.kroon.its.libraryservice.entity.BookCopy;
import com.kroon.its.libraryservice.entity.Loan;
import com.kroon.its.libraryservice.enums.BookStatus;
import com.kroon.its.libraryservice.enums.LoanStatus;
import com.kroon.its.libraryservice.exception.BusinessException;
import com.kroon.its.libraryservice.exception.ResourceNotFoundException;
import com.kroon.its.libraryservice.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;


    @Transactional
    public LoanResponse borrowBook(LoanCreateRequest request, String userId) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (request.loanDays() < 1 || request.loanDays() > 60) {
            throw new IllegalArgumentException("Loan period must be between 1 and 60 days");
        }

        BookCopy copy = loanRepository.findBookCopyForUpdate(request.bookCopyId())
                .orElseThrow(() -> new ResourceNotFoundException("Book copy not found: " + request.bookCopyId()));

        if (copy.getStatus() != BookStatus.AVAILABLE) {
            throw new BusinessException("Book copy is not available");
        }

        Instant borrowedAt = Instant.now();
        Instant dueDate = borrowedAt.plusSeconds(request.loanDays() * 24L * 60 * 60);

        copy.setStatus(BookStatus.BORROWED);

        Loan loan = Loan.builder()
                .userId(userId)
                .bookCopy(copy)
                .borrowedAt(borrowedAt)
                .dueDate(dueDate)
                .status(LoanStatus.ACTIVE)
                .build();

        return toResponse(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnBook(Long loanId, String userId) {

        Loan loan = loanRepository.findById(loanId).orElseThrow(() ->
                new ResourceNotFoundException("Loan not found: " + loanId));

        if (!loan.getUserId().equals(userId)) {
            throw new BusinessException("You cannot return another user's loan");
        }

        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new BusinessException("Loan has already been returned");
        }

        BookCopy copy = loanRepository.findBookCopyForUpdate(loan.getBookCopy().getId()
        ).orElseThrow(() -> new ResourceNotFoundException("Book copy not found"));

        loan.setReturnedAt(Instant.now());
        loan.setStatus(LoanStatus.RETURNED);
        copy.setStatus(BookStatus.AVAILABLE);

        return toResponse(loan);
    }

    public PageResponse<LoanResponse> getMyLoans(String userId, int page, int size) {

        validatePagination(page, size);

        Page<Loan> loans = loanRepository.findByUserId(userId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowedAt")));
        return toPageResponse(loans);
    }

    public LoanResponse getMyLoanById(Long loanId, String userId) {
        Loan loan = findLoan(loanId);
        if (!loan.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Loan not found: " + loanId);
        }

        return toResponse(loan);
    }

    public PageResponse<LoanResponse> getOverdueLoans(int page, int size) {

        validatePagination(page, size);

        Page<Loan> loans = loanRepository.findByStatus(LoanStatus.OVERDUE,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate")));

        return toPageResponse(loans);
    }

    private Loan findLoan(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + id));
    }

    private LoanResponse toResponse(Loan loan) {
        BookCopy copy = loan.getBookCopy();
        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .bookId(copy.getId())
                .inventoryNumber(copy.getInventoryNumber())
                .borrowedAt(loan.getBorrowedAt())
                .dueDate(loan.getDueDate())
                .returnedAt(loan.getReturnedAt())
                .status(loan.getStatus())
                .build();

    }

    private PageResponse<LoanResponse> toPageResponse(Page<Loan> page) {
        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    private void validatePagination(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
    }
}
