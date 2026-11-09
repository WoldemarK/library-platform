package com.kroon.its.libraryservice.rest;

import com.kroon.its.libraryservice.dto.request.GenreCreateRequest;
import com.kroon.its.libraryservice.dto.response.GenreResponse;
import com.kroon.its.libraryservice.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreResponse create(@Valid @RequestBody GenreCreateRequest request) {
        return genreService.create(request);
    }

    @GetMapping("/{id}")
    public GenreResponse getById(@PathVariable Long id) {
        return genreService.getById(id);
    }

    @GetMapping
    public List<GenreResponse> getAll() {
        return genreService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        genreService.delete(id);
    }
}
