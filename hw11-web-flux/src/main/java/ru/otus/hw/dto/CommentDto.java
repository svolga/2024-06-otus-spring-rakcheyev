package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto {

    private String id;

    @NotBlank
    @Size(min = 2, max = 10)
    private String text;

    private BookDto bookDto;
}
