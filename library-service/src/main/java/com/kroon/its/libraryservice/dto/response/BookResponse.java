package com.kroon.its.libraryservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record BookResponse
        (
                Long id,
                String title,
                String isbn,
                String description,
                Integer publicationYear,

                List<AuthorResponse> authors,
                List<GenreResponse> genres,

                long totalCopies,
                long availableCopies
        ) {
}
