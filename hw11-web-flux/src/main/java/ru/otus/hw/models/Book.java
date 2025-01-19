package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString(exclude = {"genres", "author"})
@Document(collection = "books")
public class Book {
    @Id
    private String id;

    private String title;

    private Author author;

    private List<Genre> genres;

    public Book(String id, String title, Author author, Genre... genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = Arrays.asList(genres);
    }
}
