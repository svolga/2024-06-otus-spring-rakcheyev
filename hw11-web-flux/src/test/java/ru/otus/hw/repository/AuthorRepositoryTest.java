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
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private static final String FIRST_AUTHOR_ID = "1";
    private static final String FIRST_AUTHOR_NAME = "Author_1";

    @Test
    void shouldSetIdOnSave() {
        Mono<Author> authorMono = authorRepository.save(new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME));

        StepVerifier
                .create(authorMono)
                .assertNext(author -> assertNotNull(author.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        List<Author> authors = mongoTemplate.findAll(Author.class).toStream().toList();
        Author expectedAuthor = authors.get(0);

        Mono<Author> authorMono = authorRepository.findById(expectedAuthor.getId());

        StepVerifier
                .create(authorMono)
                .assertNext(author -> assertThat(author)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedAuthor))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        Flux<Author> authorsFlux = authorRepository.findAll();

        List<Author> authors = authorsFlux.toStream().toList();
        assertEquals(3, authorsFlux.toStream().toList().size());
        authors.forEach(System.out::println);
    }
}
