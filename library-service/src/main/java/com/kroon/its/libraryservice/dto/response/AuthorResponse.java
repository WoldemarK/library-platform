package com.kroon.its.libraryservice.dto.response;

import lombok.Builder;

@Builder
public record AuthorResponse
        (
                Long id,
                String firstName,
                String lastName,
                String biography
        ) {
}
