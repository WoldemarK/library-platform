package com.kroon.its.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest
        (
                @NotBlank
                @Size(max = 320)
                String email,

                @NotBlank
                @Size(min = 2, max = 9)
                String password
        ) {
}
