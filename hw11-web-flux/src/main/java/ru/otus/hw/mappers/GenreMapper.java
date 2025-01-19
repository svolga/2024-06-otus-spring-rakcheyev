package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    GenreDto toDto(Genre genre);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    Genre toEntity(GenreDto genreDto);

    List<GenreDto> toDtos(List<Genre> genres);
}
