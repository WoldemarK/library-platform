package com.kroon.its.libraryservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreCreateRequest
        (
                @NotBlank(message = "Genre name is required")
                @Size(max = 100)
                String name,

                @Size(max = 5000)
                String description
        ) {
}
