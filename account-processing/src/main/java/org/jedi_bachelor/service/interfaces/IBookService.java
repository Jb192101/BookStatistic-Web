package org.jedi_bachelor.service.interfaces;

import org.jedi_bachelor.model.entities.Book;
import org.jedi_bachelor.model.enums.GenreType;

import java.util.List;
import java.util.Optional;

public interface IBookService {
    void addNewBook(Book book);
    void updateBook(Book book);
    Optional<Book> getBookById(Long id);
    List<Book> getAllBooks();
    List<Book> getBooksOfSuchGenre(GenreType genre);
    List<Book> getBooksOfSuchAuthor(String author);
    List<Book> getBooksOfSuchTitle(String title);
}
