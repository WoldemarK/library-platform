package com.kroon.its.libraryservice.service;

import com.kroon.its.libraryservice.dto.request.GenreCreateRequest;
import com.kroon.its.libraryservice.dto.response.GenreResponse;
import com.kroon.its.libraryservice.entity.Genre;
import com.kroon.its.libraryservice.exception.ResourceNotFoundException;
import com.kroon.its.libraryservice.mapper.GenreMapper;
import com.kroon.its.libraryservice.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Transactional
    public GenreResponse create(GenreCreateRequest request) {

        if (genreRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new IllegalStateException("Genre already exists: " + request.name());
        }

        Genre genre = Genre.builder()
                .name(request.name().trim())
                .description(request.description())
                .build();

        return genreMapper.toResponse(genreRepository.save(genre));
    }

    public GenreResponse getById(Long id) {

        Genre genre = findGenre(id);

        return genreMapper.toResponse(genre);
    }

    public List<GenreResponse> getAll() {
        return genreRepository.findAll()
                .stream()
                .map(genreMapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Genre genre = findGenre(id);

        if (!genre.getBooks().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete genre associated with books"
            );
        }

        genreRepository.delete(genre);
    }

    private Genre findGenre(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found: " + id));
    }
}
