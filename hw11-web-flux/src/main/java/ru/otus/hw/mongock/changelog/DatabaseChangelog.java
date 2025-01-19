package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import lombok.val;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Comment;

@ChangeLog
public class DatabaseChangelog {

    private static final String AUTHOR_NAME = "svolga";

    private Author author1;

    private Author author2;

    private Author author3;

    private Genre genre1;

    private Genre genre2;

    private Genre genre3;

    private Genre genre4;

    private Genre genre5;

    private Genre genre6;

    private Book book1;

    private Book book2;

    @ChangeSet(order = "001", id = "dropDb", author = AUTHOR_NAME, runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = AUTHOR_NAME, runAlways = true)
    public void insertAuthors(MongockTemplate repository) {
        author1 = repository.save(new Author("1", "Author_1"));
        author2 = repository.save(new Author("2", "Author_2"));
        author3 = repository.save(new Author("3", "Author_3"));
    }

    @ChangeSet(order = "003", id = "insertGenres", author = AUTHOR_NAME, runAlways = true)
    public void insertGenres(MongockTemplate repository) {
        genre1 = repository.save(new Genre("1", "Genre_1"));
        genre2 = repository.save(new Genre("2", "Genre_2"));
        genre3 = repository.save(new Genre("3", "Genre_3"));
        genre4 = repository.save(new Genre("4", "Genre_4"));
        genre5 = repository.save(new Genre("5", "Genre_5"));
        genre6 = repository.save(new Genre("6", "Genre_6"));
    }

    @ChangeSet(order = "004", id = "insertBooks", author = AUTHOR_NAME, runAlways = true)
    public void insertBooks(MongockTemplate repository) {
        book1 = new Book("1", "BookTitle_1", author1, genre1, genre2);
        repository.save(book1);
        book2 = new Book("2", "BookTitle_2", author2, genre3, genre4);
        repository.save(book2);
        val book3 = new Book("3", "BookTitle_3", author3, genre5, genre6);
        repository.save(book3);
    }

    @ChangeSet(order = "005", id = "insertComments", author = AUTHOR_NAME, runAlways = true)
    public void insertComments(MongockTemplate repository) {
        val comment1 = new Comment("1", "Book1_Comment_1", book1);
        repository.save(comment1);
        val comment2 = new Comment("2", "Book1_Comment_2", book1);
        repository.save(comment2);
        val comment3 = new Comment("3", "Book2_Comment_3", book2);
        repository.save(comment3);
    }
}