package com.kroon.its.libraryservice.dto.request;

import jakarta.validation.constraints.*;

import java.util.Set;

public record BookCreateRequest
        (
                @NotBlank(message = "Title is required")
                @Size(max = 255)
                String title,

                @NotBlank(message = "ISBN is required")
                @Size(max = 20)
                String isbn,

                @Size(max = 5000)
                String description,

                @Min(0)
                @Max(2100)
                Integer publicationYear,

                @NotEmpty(message = "At least one author is required")
                Set<@NotNull @Positive Long> authorIds,

                Set<@NotNull @Positive Long> genreIds
        ) {
}
