package com.kroon.its.authservice.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RegistrationResponse
        (
                UUID id,
                String email,
                Instant createdAt
        ) {
}
