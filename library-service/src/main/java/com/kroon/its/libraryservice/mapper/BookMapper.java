package com.kroon.its.libraryservice.mapper;

import com.kroon.its.libraryservice.dto.response.BookResponse;
import com.kroon.its.libraryservice.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final AuthorMapper authorMapper;
    private final GenreMapper genreMapper;


    public BookResponse toResponse(Book book, long totalCopies, long availableCopies) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .publicationYear(book.getPublicationYear())
                .authors(
                        book.getAuthors().stream()
                                .map(authorMapper::toResponse)
                                .toList()
                )
                .genres(
                        book.getGenres().stream()
                                .map(genreMapper::toResponse)
                                .toList()
                )
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}
