package com.kroon.its.libraryservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookCopyCreateRequest
        (
                @NotNull
                Long bookId,

                @NotBlank
                @Size(max = 100)
                String inventoryNumber
        ) {
}
