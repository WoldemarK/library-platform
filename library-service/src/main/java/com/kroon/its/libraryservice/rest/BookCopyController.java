package com.kroon.its.libraryservice.rest;

import com.kroon.its.libraryservice.dto.request.BookCopyCreateRequest;
import com.kroon.its.libraryservice.dto.response.BookCopyResponse;
import com.kroon.its.libraryservice.service.BookCopyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookCopyResponse create(@Valid @RequestBody BookCopyCreateRequest request) {
        return bookCopyService.create(request);
    }

    @GetMapping("/{id}")
    public BookCopyResponse getById(@PathVariable Long id) {
        return bookCopyService.getById(id);
    }

    @GetMapping("/book/{bookId}")
    public List<BookCopyResponse> getByBookId(@PathVariable Long bookId) {
        return bookCopyService.getByBookId(bookId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookCopyService.delete(id);
    }
}
