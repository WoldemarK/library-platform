package com.kroon.its.libraryservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoanCreateRequest
        (
                @NotNull(message = "Book copy ID is required")
                @Positive(message = "Book copy ID must be positive")
                Long bookCopyId,

                @NotNull
                @Positive
                Integer loanDays
        ) {
}
