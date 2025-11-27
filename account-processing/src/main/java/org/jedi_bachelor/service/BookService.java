package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jedi_bachelor.model.entities.Book;
import org.jedi_bachelor.model.enums.GenreType;
import org.jedi_bachelor.repository.BookRepository;
import org.jedi_bachelor.service.interfaces.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService implements IBookService {
    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private final LevenshteinDistance levenshtein;

    /*
    * Метод для добавления новой книги
    * @param book (Book) - книга, которую надо добавить
     */
    @Override
    public void addNewBook(Book book) {
        bookRepository.save(book);
    }

    /*
    * Метод для обновления книги
    * @param book (Book) - книга, которую надо обновить
     */
    @Override
    public void updateBook(Book book) {
        if(bookRepository.existsById(book.getId())) {
            bookRepository.save(book);
        }
    }

    /*
    * Метод получения книги по её ID
    * @param id (Long) - ID книги
    * @return book (Optional<Book>) - книга с таким ID, если есть
     */
    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    /*
    * Метод получения всех книг
    * @return books (List<Book>) - список всех книг из таблицы
     */
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /*
    * Метод для поиска книг определённого жанра
    * @param genre (GenreType) - жанр
    * @return neededBooks (List<Book>) - список книг, удовлетворяющих условию
     */
    @Override
    public List<Book> getBooksOfSuchGenre(GenreType genre) {
        List<Book> books = this.bookRepository.findAll();

        List<Book> neededBooks = new ArrayList<>();
        for(Book book : books) {
            if(book.getGenreType().equals(genre)) {
                neededBooks.add(book);
            }
        }

        return neededBooks;
    }

    /*
    * Метод получения книг определённого автора
    * @param author (String) - часть имени или целое имя автора
    * @return neededBooks (List<Book>) - список книг, которые удовлетворяют условию
     */
    @Override
    public List<Book> getBooksOfSuchAuthor(String author) {
        List<Book> books = this.bookRepository.findAll();

        String authorLowerCase = author.toLowerCase();

        List<Book> neededBooks = new ArrayList<>();
        String lowerCase;

        int searchedDistance;
        for(Book book : books) {
            lowerCase = book.getAuthor().toLowerCase();
            if(lowerCase.contains(authorLowerCase)) {
                neededBooks.add(book);
            }

            searchedDistance = levenshtein.apply(lowerCase, authorLowerCase);

            if(searchedDistance <= 4) {
                neededBooks.add(book);
            }
        }

        return neededBooks;
    }

    /*
    * Метод получения книг с определённым фрагментом в названии/определённым заголовком
    * @param title (String) - фрагмент или заголовок
    * @return neededBooks (List<Book>) - список книг, удовлетворяющих условию
     */
    @Override
    public List<Book> getBooksOfSuchTitle(String title) {
        List<Book> books = this.bookRepository.findAll();

        String titleLowerCase = title.toLowerCase();

        List<Book> neededBooks = new ArrayList<>();
        String lowerCase;

        int searchedDistance;
        for(Book book : books) {
            lowerCase = book.getTitle().toLowerCase();
            if(lowerCase.contains(titleLowerCase)) {
                neededBooks.add(book);
                continue;
            }

            searchedDistance = levenshtein.apply(lowerCase, titleLowerCase);

            if(searchedDistance <= 4) {
                neededBooks.add(book);
            }
        }

        return neededBooks;
    }
}
