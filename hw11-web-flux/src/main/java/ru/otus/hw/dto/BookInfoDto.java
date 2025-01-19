package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookInfoDto {
    private String id;
    private String title;
    private String authorTitle;
    private String genreTitles;
}
