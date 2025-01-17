package ru.otus.hw.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInfoDto;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

import static org.reflections.Reflections.log;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final GenreRepository genreRepository;

    private final AuthorRepository authorRepository;

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @GetMapping("/api/v1/book")
    public Flux<BookInfoDto> getBooks(){
        return bookRepository.findAll().map(bookMapper::toBookInfoDto);
    }

    @GetMapping("/api/v1/book/{id}")
    public Mono<ResponseEntity<BookDto>> getBook(@PathVariable String id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/api/v1/book/{id}")
    public Mono<ResponseEntity<String>> deleteBook(@PathVariable("id") String id) {
        return bookRepository.findById(id)
                .flatMap(book -> bookRepository.deleteById(id).thenReturn(book))
                .map(book -> {
                    log.info("Deleted book: {}", book);
                    return ResponseEntity.status(HttpStatus.OK).body("Book: " + book.getTitle() + " deleted!");
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("bookId: " + id + " Not found"));
    }

    @PostMapping("/api/v1/book")
    public Mono<ResponseEntity<BookDto>>  createBook(@Valid @RequestBody BookDto bookDto) {
        bookDto.setId(null);
        return saveBook(bookDto, bookDto.getAuthorDto().getId(), bookDto.getGenreDtos())
                .map(book -> new ResponseEntity<>(bookMapper.toDto(book), HttpStatus.OK));
    }

    @PutMapping("/api/v1/book")
    public Mono<ResponseEntity<BookDto>> updateBook(@Valid @RequestBody BookDto bookDto) {
        return saveBook(bookDto, bookDto.getAuthorDto().getId(), bookDto.getGenreDtos())
                .map(book -> new ResponseEntity<>(bookMapper.toDto(book), HttpStatus.OK))
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    private Mono<Book> saveBook(BookDto bookDto, String authorId, Set<String> genresIds) {
        var genresFlux = genreRepository.findAllById(genresIds).collectList();
        var authorMono = authorRepository.findById(authorId);

        return Mono.zip(genresFlux, authorMono,
                        (genres, author) -> new Book(bookDto.getId(), bookDto.getTitle(),
                                author, genres))
                .flatMap(bookRepository::save);
    }

}