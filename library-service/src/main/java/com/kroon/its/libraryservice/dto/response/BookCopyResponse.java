package com.kroon.its.libraryservice.dto.response;

import com.kroon.its.libraryservice.enums.BookStatus;

public record BookCopyResponse
        (
                Long id,

                Long bookId,

                String bookTitle,

                String inventoryNumber,

                BookStatus status
        ) {
}
