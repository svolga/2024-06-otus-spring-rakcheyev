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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.mappers.AuthorMapperImpl;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Контроллер Author должен ")
@Import(AuthorMapperImpl.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper mapper;

    private static final String FIRST_AUTHOR_ID = "1";
    private static final String FIRST_AUTHOR_NAME = "Author_1";

    private static final String SECOND_AUTHOR_ID = "2";
    private static final String SECOND_AUTHOR_NAME = "Author_2";

    private static final String UPDATED_AUTHOR_ID = "1000";
    private static final String UPDATED_AUTHOR_NAME = "Updated Author";

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(AuthorMapper.class);
    }

    @Test
    @DisplayName("возвращать список авторов")
    void shouldReturnCorrectAuthorList() {

        List<Author> authorsExpected = List.of(
                new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME),
                new Author(SECOND_AUTHOR_ID, SECOND_AUTHOR_NAME)
        );

        given(authorRepository.findAll())
                .willReturn(Flux.fromIterable(authorsExpected));

        var result = webTestClient.get().uri("/api/v1/author")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<AuthorDto> stepResult = null;
        for (var author : authorsExpected) {
            stepResult = step.expectNext(mapper.toDto(author));
        }

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    @DisplayName("возвращать автора по id")
    void shouldReturnCorrectAuthorById() {

        var author = new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME);

        given(authorRepository.findById(FIRST_AUTHOR_ID)).willReturn(Mono.just(author));

        var result = webTestClient.get().uri("/api/v1/author/%s".formatted(author.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<AuthorDto> stepResult = step.expectNext(mapper.toDto(author));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @DisplayName("создавать автора")
    @Test
    void shouldCreateAuthor() {

        var author = new Author(null, FIRST_AUTHOR_NAME);
        var authorExpected = new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME);

        given(authorRepository.save(author)).willReturn(Mono.just(authorExpected));

        var result = webTestClient
                .post().uri("/api/v1/author")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorExpected)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<AuthorDto> stepResult = step.expectNext(mapper.toDto(authorExpected));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @DisplayName("обновлять автора")
    @Test
    void shouldUpdateAuthor() {

        var author = new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME);
        var authorUpdated = new Author(author.getId(), UPDATED_AUTHOR_NAME);

        given(authorRepository.existsById(author.getId())).willReturn(Mono.just(true));
        given(authorRepository.save(authorUpdated)).willReturn(Mono.just(authorUpdated));

        var result = webTestClient
                .put().uri("/api/v1/author")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorUpdated)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<AuthorDto> stepResult = step.expectNext(mapper.toDto(authorUpdated));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @DisplayName("возвращать статус 404 при несуществующем id автора")
    @Test
    void shouldNotUpdateAuthorWIthNotValidAuthorId() {

        var authorUpdated = new Author(UPDATED_AUTHOR_ID, UPDATED_AUTHOR_NAME);

        given(authorRepository.existsById(authorUpdated.getId())).willReturn(Mono.just(false));
        given(authorRepository.save(authorUpdated)).willReturn(Mono.empty());

        webTestClient
                .put().uri("/api/v1/author")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorUpdated)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("удалять автора по id")
    void shouldCorrectDeleteAuthor() {
        var author = new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME);

        given(authorRepository.deleteById(author.getId())).willReturn(Mono.empty());

        var result = webTestClient
                .delete().uri("/api/v1/author/%s".formatted(author.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .returnResult(String.class)
                .getResponseBody();

        verify(authorRepository, times(1)).deleteById(author.getId());
        assertThat(result).isNotNull();

    }

}