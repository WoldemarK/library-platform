package com.library.contracts.book;

import java.time.Instant;
import java.util.UUID;


public record BookIssuedEvent
        (
                UUID eventId,
                UUID loanId,
                UUID userId,
                UUID bookId,
                Instant issuedAt,
                Instant dueDate,
                int schemaVersion
        ) {
    public static final int CURRENT_VERSION = 1;
}
