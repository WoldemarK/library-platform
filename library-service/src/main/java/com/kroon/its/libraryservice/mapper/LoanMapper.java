package com.kroon.its.libraryservice.mapper;

import com.kroon.its.libraryservice.dto.response.LoanResponse;
import com.kroon.its.libraryservice.entity.Book;
import com.kroon.its.libraryservice.entity.BookCopy;
import com.kroon.its.libraryservice.entity.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanResponse toResponse(Loan loan) {

        BookCopy bookCopy = loan.getBookCopy();
        Book book = bookCopy.getBook();

        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .bookCopyId(bookCopy.getId())
                .inventoryNumber(bookCopy.getInventoryNumber())
                .bookTitle(book.getTitle())
                .bookId(book.getId())
                .borrowedAt(loan.getBorrowedAt())
                .dueDate(loan.getDueDate())
                .returnedAt(loan.getReturnedAt())
                .status(loan.getStatus())
                .build();

    }
}
