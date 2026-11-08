package com.kroon.its.libraryservice.dto.request;

import jakarta.validation.constraints.*;

import java.util.Set;

public record BookUpdateRequest
        (
                @Size(min = 1, max = 255)
                String title,

                @Size(min = 1, max = 20)
                String isbn,

                @Size(max = 5000)
                String description,

                @Min(0)
                @Max(2100)
                Integer publicationYear,

                Set<@NotNull @Positive Long> authorIds,

                Set<@NotNull @Positive Long> genreIds
        ) {
}
