package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    AuthorDto toDto(Author author);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    Author toEntity(AuthorDto authorDto);

    List<AuthorDto> toDtos(List<Author> authors);
}