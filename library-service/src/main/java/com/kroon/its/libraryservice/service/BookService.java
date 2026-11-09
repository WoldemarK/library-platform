package com.kroon.its.libraryservice.service;

import com.kroon.its.libraryservice.dto.request.BookCreateRequest;
import com.kroon.its.libraryservice.dto.request.BookUpdateRequest;
import com.kroon.its.libraryservice.dto.response.BookResponse;
import com.kroon.its.libraryservice.dto.response.PageResponse;
import com.kroon.its.libraryservice.entity.Author;
import com.kroon.its.libraryservice.entity.Book;
import com.kroon.its.libraryservice.entity.BookCopy;
import com.kroon.its.libraryservice.entity.Genre;
import com.kroon.its.libraryservice.enums.BookStatus;
import com.kroon.its.libraryservice.exception.ResourceAlreadyExistsException;
import com.kroon.its.libraryservice.exception.ResourceNotFoundException;
import com.kroon.its.libraryservice.mapper.BookMapper;
import com.kroon.its.libraryservice.repository.AuthorRepository;
import com.kroon.its.libraryservice.repository.BookCopyRepository;
import com.kroon.its.libraryservice.repository.BookRepository;
import com.kroon.its.libraryservice.repository.GenreRepository;
import com.kroon.its.libraryservice.repository.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final BookCopyRepository bookCopyRepository;

    @Transactional
    public BookResponse create(BookCreateRequest request) {

        String isbn = request.isbn().trim();

        if (bookRepository.existsByIsbn(isbn)) {
            throw new ResourceAlreadyExistsException(
                    "Book with ISBN already exists: " + isbn
            );
        }

        Set<Author> authors = findAuthors(request.authorIds());
        Set<Genre> genres = findGenres(request.genreIds());

        Book book = Book.builder()
                .title(request.title().trim())
                .isbn(isbn)
                .description(request.description())
                .publicationYear(request.publicationYear())
                .authors(authors)
                .genres(genres)
                .build();

        Book savedBook = bookRepository.save(book);

        List<BookCopy> copies = new ArrayList<>();

        for (int i = 0; i < request.copiesCount(); i++) {
            BookCopy copy = BookCopy.builder()
                    .book(savedBook)
                    .inventoryNumber(generateInventoryNumber())
                    .status(BookStatus.AVAILABLE)
                    .build();
            copies.add(copy);
        }

        bookCopyRepository.saveAll(copies);

        return bookMapper.toResponse(savedBook, copies.size(), copies.size());
    }

    private String generateInventoryNumber() {
        return "INV-" + UUID.randomUUID();
    }

    public BookResponse getById(Long id) {
        Book book = findBook(id);
        return toResponse(book);
    }

    @Transactional
    public BookResponse update(Long id, BookUpdateRequest request) {
        Book book = findBook(id);

        validateUpdateRequest(id, request, book);

        return toResponse(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {

        Book book = findBook(id);

        if (bookCopyRepository.countByBookId(id) > 0) {
            throw new IllegalStateException("Cannot delete book with existing copies");
        }

        bookRepository.delete(book);
    }

    private void validateUpdateRequest(Long id, BookUpdateRequest request, Book book) {
        if (request.title() != null) {
            book.setTitle(request.title().trim());
        }

        if (request.isbn() != null) {

            String isbn = request.isbn().trim();

            if (isbn.isBlank()) {
                throw new IllegalArgumentException("ISBN cannot be blank");
            }

            if (bookRepository.existsByIsbnAndIdNot(isbn, id)) {
                throw new IllegalStateException("Book with ISBN already exists: " + isbn);
            }

            book.setIsbn(isbn);
        }

        if (request.description() != null) {
            book.setDescription(request.description());
        }

        if (request.publicationYear() != null) {
            book.setPublicationYear(request.publicationYear());
        }

        if (request.authorIds() != null) {
            book.setAuthors(findAuthors(request.authorIds()));
        }

        if (request.genreIds() != null) {
            book.setGenres(findGenres(request.genreIds()));
        }
    }

    public PageResponse<BookResponse> search(String title,
                                             String isbn,
                                             Long authorId,
                                             Long genreId,
                                             int page,
                                             int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException("Page mustn't be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Specification<Book> specification = BookSpecification.titleContains(title)
                .and(BookSpecification.isbnContains(isbn))
                .and(BookSpecification.authorIdEquals(authorId))
                .and(BookSpecification.genreIdEquals(genreId));

        Page<Book> result = bookRepository.findAll(specification,
                PageRequest.of(page, size,
                        Sort.by(
                                Sort.Direction.ASC, "title")
                )
        );

        return PageResponse.<BookResponse>builder()
                .content(
                        result.getContent()
                                .stream()
                                .map(this::toResponse)
                                .toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID: " + id));
    }

    private BookResponse toResponse(Book book) {
        long totalCopies = bookCopyRepository.countByBookId(book.getId());
        long availableCopies = bookCopyRepository.countByBookIdAndStatus(
                book.getId(),
                BookStatus.AVAILABLE
        );
        return bookMapper.toResponse(book, totalCopies, availableCopies);
    }

    private Set<Author> findAuthors(Set<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Author> authors = new HashSet<>(authorRepository.findAllById(ids));

        if (authors.size() != ids.size()) {
            throw new ResourceNotFoundException(
                    "One or more authors were not found");
        }
        return authors;
    }

    private Set<Genre> findGenres(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(ids));

        if (genres.size() != ids.size()) {
            throw new ResourceNotFoundException(
                    "One or more genres were not found"
            );
        }

        return genres;
    }
}
