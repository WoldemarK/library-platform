package com.kroon.its.libraryservice.mapper;

import com.kroon.its.libraryservice.dto.response.GenreResponse;
import com.kroon.its.libraryservice.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreResponse toResponse(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }
}
