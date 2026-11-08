package com.kroon.its.libraryservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorCreateRequest
        (
                @NotBlank(message = "First name is required")
                @Size(max = 100)
                String firstName,

                @NotBlank(message = "Last name is required")
                @Size(max = 100)
                String lastName,

                @Size(max = 5000)
                String biography
        ) {
}
