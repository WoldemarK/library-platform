package com.kroon.its.libraryservice.dto.response;

import com.kroon.its.libraryservice.enums.LoanStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record LoanResponse
        (
                Long id,
                String userId,

                Long bookCopyId,
                String inventoryNumber,

                Long bookId,
                String bookTitle,

                Instant borrowedAt,
                Instant dueDate,
                Instant returnedAt,

                LoanStatus status
        ) {
}
