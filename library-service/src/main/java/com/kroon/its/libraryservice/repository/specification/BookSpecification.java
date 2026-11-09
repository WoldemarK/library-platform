package com.kroon.its.libraryservice.repository.specification;

import com.kroon.its.libraryservice.entity.Book;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecification {

    private BookSpecification() {
    }

    public static Specification<Book> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("title")),
                    "%" + title.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<Book> isbnContains(String isbn) {
        return (root, query, cb) -> {
            if (isbn == null || isbn.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("isbn")),
                    "%" + isbn.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<Book> authorIdEquals(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null) {
                return cb.conjunction();
            }

            Join<Object, Object> authors = root.join("authors", JoinType.INNER);

            query.distinct(true);

            return cb.equal(authors.get("id"), authorId);
        };
    }

    public static Specification<Book> genreIdEquals(Long genreId) {
        return (root, query, cb) -> {
            if (genreId == null) {
                return cb.conjunction();
            }

            Join<Object, Object> genres = root.join("genres", JoinType.INNER);

            query.distinct(true);

            return cb.equal(genres.get("id"), genreId);
        };
    }
}
