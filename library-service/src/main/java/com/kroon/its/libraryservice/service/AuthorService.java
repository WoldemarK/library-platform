package com.kroon.its.libraryservice.service;

import com.kroon.its.libraryservice.dto.request.AuthorCreateRequest;
import com.kroon.its.libraryservice.dto.response.AuthorResponse;
import com.kroon.its.libraryservice.entity.Author;
import com.kroon.its.libraryservice.exception.ResourceNotFoundException;
import com.kroon.its.libraryservice.mapper.AuthorMapper;
import com.kroon.its.libraryservice.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Transactional
    public AuthorResponse create(AuthorCreateRequest request) {

        Author author = Author.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .biography(request.biography())
                .build();

        Author savedAuthor = authorRepository.save(author);

        return authorMapper.toResponse(authorRepository.save(savedAuthor));

    }

    public AuthorResponse getById(Long id) {
        Author author = findAuthor(id);
        return authorMapper.toResponse(author);
    }

    private Author findAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }

    public List<AuthorResponse> getAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {

        Author author = findAuthor(id);

        if (!author.getBooks().isEmpty()) {
            throw new IllegalStateException("Cannot delete author associated with books");
        }
        authorRepository.deleteById(id);
    }
}
