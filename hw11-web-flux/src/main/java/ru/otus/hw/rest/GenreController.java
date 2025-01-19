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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mappers.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @GetMapping("/api/v1/genre")
    public Flux<GenreDto> getGenres() {
        return genreRepository.findAll().map(genreMapper::toDto);
    }

    @GetMapping("/api/v1/genre/{id}")
    public Mono<ResponseEntity<GenreDto>> getGenre(@PathVariable String id) {
        return genreRepository.findById(id)
                .map(genreMapper::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping("/api/v1/genre")
    public Mono<GenreDto> createGenre(@Valid @RequestBody GenreDto genre) {
        genre.setId(null);
        return genreRepository.save(genreMapper.toEntity(genre))
                .map(genreMapper::toDto);
    }

    @PutMapping("/api/v1/genre")
    public Mono<ResponseEntity<GenreDto>> updateGenre(@Valid @RequestBody GenreDto dto) {
        return Mono.just(dto)
                .filterWhen(genreDto -> genreRepository.existsById(genreDto.getId()))
                .flatMap(genreDto -> genreRepository.save(genreMapper.toEntity(genreDto)))
                .map(author -> new ResponseEntity<>(genreMapper.toDto(author), HttpStatus.OK))
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/api/v1/genre/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteGenre(@PathVariable("id") String id) {
        return genreRepository.deleteById(id)
                .then(Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }

}
