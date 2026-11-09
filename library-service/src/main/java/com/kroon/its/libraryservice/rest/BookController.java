package com.kroon.its.libraryservice.rest;

import com.kroon.its.libraryservice.dto.request.BookCreateRequest;
import com.kroon.its.libraryservice.dto.request.BookUpdateRequest;
import com.kroon.its.libraryservice.dto.response.BookResponse;
import com.kroon.its.libraryservice.dto.response.PageResponse;
import com.kroon.its.libraryservice.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookCreateRequest request) {
        return bookService.create(request);
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id
    ) {
        return bookService.getById(id);
    }

    @GetMapping
    public PageResponse<BookResponse> search(@RequestParam(required = false) String title,
                                             @RequestParam(required = false) String isbn,
                                             @RequestParam(required = false) Long authorId,
                                             @RequestParam(required = false) Long genreId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size
    ) {
        return bookService.search(title, isbn, authorId, genreId, page, size);
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request
    ) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
