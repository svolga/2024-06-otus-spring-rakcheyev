package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface CommentMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "book", target = "bookDto")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDtos(List<Comment> comments);
}
