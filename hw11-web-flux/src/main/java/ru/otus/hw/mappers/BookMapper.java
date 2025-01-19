package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInfoDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "authorDto")
    @Mapping(source = "genres", target = "genreDtos", qualifiedByName = "getGenreSet")
    BookDto toDto(Book book);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "authorTitle", qualifiedByName = "getAuthorTitle")
    @Mapping(source = "genres", target = "genreTitles", qualifiedByName = "getGenreTitles")
    BookInfoDto toBookInfoDto(Book book);

    List<BookDto> toDtos(List<Book> books);
    List<BookInfoDto> toBookInfoDtos(List<Book> books);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "genres", ignore = true)
    default Book toEntity(BookDto bookDto, Author author, List<Genre> genres) {
        Book book = new Book();
        book.setId(bookDto.getId());
        book.setTitle(bookDto.getTitle());
        book.setAuthor(author);
        book.setGenres(genres);

        return book;
    }

    @Named("getAuthorTitle")
    default String getAuthorTitle(Author author) {
        return author.getFullName();
    }

    @Named("getGenreTitles")
    default String getGenreTitles(List<Genre> genres) {
        return genres.stream()
                .map(Genre::getName)
                .collect(Collectors.joining(", "));

    }

    @Named("getGenreSet")
    default Set<String> getGenreSet(List<Genre> genres) {
        return genres.stream().map(Genre::getId).collect(Collectors.toSet());
    }
}
