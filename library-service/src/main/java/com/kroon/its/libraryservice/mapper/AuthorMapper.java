package com.kroon.its.libraryservice.mapper;

import com.kroon.its.libraryservice.dto.response.AuthorResponse;
import com.kroon.its.libraryservice.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public AuthorResponse toResponse(Author author){
        return AuthorResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .biography(author.getBiography())
                .build();
    }
}
