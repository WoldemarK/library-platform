package com.kroon.its.libraryservice.service;

import com.kroon.its.libraryservice.dto.request.BookCopyCreateRequest;
import com.kroon.its.libraryservice.dto.response.BookCopyResponse;
import com.kroon.its.libraryservice.entity.Book;
import com.kroon.its.libraryservice.entity.BookCopy;
import com.kroon.its.libraryservice.enums.BookStatus;
import com.kroon.its.libraryservice.exception.ResourceNotFoundException;
import com.kroon.its.libraryservice.repository.BookCopyRepository;
import com.kroon.its.libraryservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    @Transactional
    public BookCopyResponse create(BookCopyCreateRequest request) {

        String inventoryNumber = request.inventoryNumber().trim();

        if (bookCopyRepository.existsByInventoryNumber(inventoryNumber)) {
            throw new IllegalStateException(
                    "Inventory number already exists: " + inventoryNumber
            );
        }

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Book not found: " + request.bookId()
                        )
                );

        BookCopy copy = BookCopy.builder()
                .book(book)
                .inventoryNumber(inventoryNumber)
                .status(BookStatus.AVAILABLE)
                .build();

        return toResponse(bookCopyRepository.save(copy));
    }

    public BookCopyResponse getById(Long id) {

        return toResponse(findCopy(id));
    }

    public List<BookCopyResponse> getByBookId(Long bookId) {

        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found: " + bookId);
        }

        return bookCopyRepository.findAllByBookId(bookId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {

        BookCopy copy = findCopy(id);

        if (copy.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Only available copies can be deleted");
        }

        bookCopyRepository.delete(copy);
    }

    private BookCopy findCopy(Long id) {

        return bookCopyRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException("Book copy not found: " + id));
    }

    private BookCopyResponse toResponse(BookCopy copy) {

        return new BookCopyResponse(
                copy.getId(),
                copy.getBook().getId(),
                copy.getBook().getTitle(),
                copy.getInventoryNumber(),
                copy.getStatus()
        );
    }
}
