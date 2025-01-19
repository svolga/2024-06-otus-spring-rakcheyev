package ru.otus.hw.rest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.mappers.GenreMapperImpl;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@DisplayName("Контроллер Genre должен ")
@Import(GenreMapperImpl.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GenreRepository genreRepository;

    @Autowired
    private GenreMapper mapper;

    private static final String FIRST_GENRE_ID = "1";
    private static final String FIRST_GENRE_NAME = "Genre_1";

    private static final String SECOND_GENRE_ID = "2";
    private static final String SECOND_GENRE_NAME = "Genre_2";

    private static final String UPDATED_GENRE_ID = "100";
    private static final String UPDATED_GENRE_NAME = "Updated Genre";

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(GenreMapper.class);
    }

    @Test
    @DisplayName("возвращать список жанров")
    void shouldReturnCorrectGenreList() {

        List<Genre> genresExpected = List.of(
                new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME),
                new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME)
        );

        //given
        given(genreRepository.findAll())
                .willReturn(Flux.fromIterable(genresExpected));

        var result = webTestClient.get().uri("/api/v1/genre")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<GenreDto> stepResult = null;
        for (var genre : genresExpected) {
            stepResult = step.expectNext(mapper.toDto(genre));
        }

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    @DisplayName("возвращать жанр по id")
    void shouldReturnCorrectGenreById() {

        var genre = new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME);
        given(genreRepository.findById(FIRST_GENRE_ID)).willReturn(Mono.just((genre)));

        var result = webTestClient.get().uri("/api/v1/genre/%s".formatted(genre.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<GenreDto> stepResult = step.expectNext(mapper.toDto(genre));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @DisplayName("создавать жанр")
    @Test
    void shouldCreateGenre() {
        var genre = new Genre(null, FIRST_GENRE_NAME);
        var genreExpected = new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME);

        given(genreRepository.save(genre)).willReturn(Mono.just(genreExpected));

        var result = webTestClient
                .post().uri("/api/v1/genre")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(genreExpected)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<GenreDto> stepResult = step.expectNext(mapper.toDto(genreExpected));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @DisplayName("обновлять жанр")
    @Test
    void shouldUpdateGenre() {
        var genre = new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME);
        var genreUpdated = new Genre(genre.getId(), UPDATED_GENRE_NAME);

        given(genreRepository.existsById(genre.getId())).willReturn(Mono.just(true));
        given(genreRepository.save(genreUpdated)).willReturn(Mono.just(genreUpdated));

        var result = webTestClient
                .put().uri("/api/v1/genre")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(genreUpdated)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<GenreDto> stepResult = step.expectNext(mapper.toDto(genreUpdated));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @DisplayName("возвращать статус 404 при несуществующем id жанре")
    @Test
    void shouldNotUpdateGenreWIthNotValidGenreId() {
        var genreUpdated = new Genre(UPDATED_GENRE_ID, UPDATED_GENRE_NAME);

        given(genreRepository.existsById(genreUpdated.getId())).willReturn(Mono.just(false));
        given(genreRepository.save(genreUpdated)).willReturn(Mono.empty());

        webTestClient
                .put().uri("/api/v1/genre")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(genreUpdated)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("удалять жанр по id")
    void shouldCorrectDeleteGenre() {
        var genre = new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME);
        given(genreRepository.deleteById(genre.getId())).willReturn(Mono.empty());

        var result = webTestClient
                .delete().uri("/api/v1/genre/%s".formatted(genre.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .returnResult(String.class)
                .getResponseBody();

        verify(genreRepository, times(1)).deleteById(genre.getId());
        assertThat(result).isNotNull();
    }

}