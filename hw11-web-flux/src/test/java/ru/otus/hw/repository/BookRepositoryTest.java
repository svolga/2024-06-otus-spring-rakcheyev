package ru.otus.hw.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private static final String FIRST_BOOK_ID = "1";
    private static final String FIRST_BOOK_TITLE = "Book_1";

    private static final String FIRST_GENRE_ID = "1";
    private static final String FIRST_GENRE_NAME = "Genre_1";

    private static final String FIRST_AUTHOR_ID = "1";
    private static final String FIRST_AUTHOR_NAME = "Author_1";

    @Test
    void shouldSetIdOnSave() {

        var book = new Book(
                FIRST_BOOK_ID,
                FIRST_BOOK_TITLE,
                new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME),
                List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME))
        );

        Mono<Book> bookMono = bookRepository.save(book);

        StepVerifier
                .create(bookMono)
                .assertNext(b -> assertNotNull(b.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        List<Book> books = mongoTemplate.findAll(Book.class).toStream().toList();
        Book expectedBook = books.get(0);

        Mono<Book> bookMono = bookRepository.findById(expectedBook.getId());

        StepVerifier
                .create(bookMono)
                .assertNext(book -> assertThat(book)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedBook))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        Flux<Book> booksFlux = bookRepository.findAll();

        List<Book> books = booksFlux.toStream().toList();
        assertEquals(3, booksFlux.toStream().toList().size());
        books.forEach(System.out::println);
    }

}
