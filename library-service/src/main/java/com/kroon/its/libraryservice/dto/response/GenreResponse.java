package com.kroon.its.libraryservice.dto.response;

import lombok.Builder;

@Builder
public record GenreResponse
        (
                Long id,
                String name,
                String description
        ) {
}
