package ru.otus.hw.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private static final String FIRST_GENRE_ID = "1";
    private static final String FIRST_GENRE_NAME = "Genre_1";

    @Test
    void shouldSetIdOnSave() {
        Mono<Genre> genreMono = genreRepository.save(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME));

        StepVerifier
                .create(genreMono)
                .assertNext(genre -> assertNotNull(genre.getId()))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        List<Genre> genres = mongoTemplate.findAll(Genre.class).toStream().toList();
        Genre expectedGenre = genres.get(0);

        Mono<Genre> genreMono = genreRepository.findById(expectedGenre.getId());

        StepVerifier
                .create(genreMono)
                .assertNext(genre -> assertThat(genre)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedGenre))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectAuthorsList() {
        Flux<Genre> genresFlux = genreRepository.findAll();

        List<Genre> genres = genresFlux.toStream().toList();
        assertEquals(6, genresFlux.toStream().toList().size());
        genres.forEach(System.out::println);
    }

}
