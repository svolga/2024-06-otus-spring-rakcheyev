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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInfoDto;
import ru.otus.hw.mappers.AuthorMapper;
import ru.otus.hw.mappers.AuthorMapperImpl;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.mappers.BookMapperImpl;
import ru.otus.hw.mappers.GenreMapperImpl;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Контроллер Book должен ")
@Import({BookMapperImpl.class, AuthorMapperImpl.class, GenreMapperImpl.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private CommentRepository commentRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private AuthorMapper authorMapper;

    private static final String FIRST_BOOK_ID = "1";
    private static final String FIRST_BOOK_TITLE = "Book_1";

    private static final String BOOK_TITLE_UPDATED = "BookTitle_Updated";

    private static final String FIRST_GENRE_ID = "1";
    private static final String FIRST_GENRE_NAME = "Genre_1";

    private static final String SECOND_GENRE_ID = "2";
    private static final String SECOND_GENRE_NAME = "Genre_2";

    private static final String FIRST_AUTHOR_ID = "1";
    private static final String FIRST_AUTHOR_NAME = "Author_1";

    @BeforeEach
    void setup() {
        bookMapper = Mappers.getMapper(BookMapper.class);
        authorMapper = Mappers.getMapper(AuthorMapper.class);
    }

    @Test
    @DisplayName("возвращать список книг")
    void shouldReturnCorrectBookList() {

        List<Book> booksExpected = List.of(
                new Book(
                        FIRST_BOOK_ID,
                        FIRST_BOOK_TITLE,
                        new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME),
                        List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME))
                )
        );

        given(bookRepository.findAll())
                .willReturn(Flux.fromIterable(booksExpected));

        var result = webTestClient.get().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookInfoDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<BookInfoDto> stepResult = null;
        for (var book : booksExpected) {
            stepResult = step.expectNext(bookMapper.toBookInfoDto(book));
        }

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    @DisplayName("возвращать книгу по id")
    void shouldReturnCorrectBookById() {

        var book = new Book(
                FIRST_BOOK_ID,
                FIRST_BOOK_TITLE,
                new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME),
                List.of(new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME))
        );

        given(bookRepository.findById(FIRST_BOOK_ID)).willReturn(Mono.just(book));

        var result = webTestClient.get().uri("/api/v1/book/%s".formatted(book.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<BookDto> stepResult = step.expectNext(bookMapper.toDto(book));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @DisplayName("создавать книгу")
    @Test
    void shouldCreateBook() {
        var author = getFirstAuthor();
        var genres = getGenres();
        var genresSet = getGenresSet();

        var book = new Book(null, FIRST_BOOK_TITLE, author, genres);
        var bookDto = new BookDto(FIRST_BOOK_ID, FIRST_BOOK_TITLE, authorMapper.toDto(author), genresSet);

        given(genreRepository.findAllById(anyIterable())).willReturn(Flux.fromIterable(genres));
        given(authorRepository.findById(anyString())).willReturn(Mono.just(author));
        given(bookRepository.save(book)).willReturn(Mono.just(book));

        var result = webTestClient
                .post().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<BookDto> stepResult = step.expectNext(bookMapper.toDto(book));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @DisplayName("обновлять книгу")
    @Test
    void shouldUpdateBook() {
        var author = getFirstAuthor();
        var genres = getGenres();
        var genresSet = getGenresSet();

        var book = new Book(FIRST_BOOK_ID, BOOK_TITLE_UPDATED, author, genres);
        var bookUpdatedDto = new BookDto(FIRST_BOOK_ID, BOOK_TITLE_UPDATED, authorMapper.toDto(author), genresSet);

        given(genreRepository.findAllById(anyIterable())).willReturn(Flux.fromIterable(genres));
        given(authorRepository.findById(anyString())).willReturn(Mono.just(author));
        given(bookRepository.save(book)).willReturn(Mono.just(book));

        var result = webTestClient
                .put().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookUpdatedDto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<BookDto> stepResult = step.expectNext(bookMapper.toDto(book));

        Assertions.assertThat(stepResult).isNotNull();
        stepResult.verifyComplete();
    }

    @Test
    @DisplayName("удалять книгу по id")
    void shouldCorrectDeleteBook() {

        var author = getFirstAuthor();
        var genres = getGenres();

        var book = new Book(FIRST_BOOK_ID, BOOK_TITLE_UPDATED, author, genres);

        given(bookRepository.deleteById(book.getId())).willReturn(Mono.empty());
        given(commentRepository.deleteAllByBookId(book.getId())).willReturn(Mono.empty());

        var result = webTestClient
                .delete().uri("/api/v1/book/%s".formatted(FIRST_BOOK_ID))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .returnResult(String.class)
                .getResponseBody();

        verify(bookRepository, times(1)).deleteById(FIRST_BOOK_ID);
        assertThat(result).isNotNull();

    }

    private static Author getFirstAuthor() {
        return new Author(FIRST_AUTHOR_ID, FIRST_AUTHOR_NAME);
    }

    private static List<Genre> getGenres() {
        return List.of(
                new Genre(FIRST_GENRE_ID, FIRST_GENRE_NAME),
                new Genre(SECOND_GENRE_ID, SECOND_GENRE_NAME)
        );
    }

    private static Set<String> getGenresSet() {
        return Set.of(FIRST_GENRE_ID, SECOND_GENRE_ID);
    }

}