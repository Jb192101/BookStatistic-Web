package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jedi_bachelor.model.entities.Book;
import org.jedi_bachelor.model.enums.GenreType;
import org.jedi_bachelor.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private final LevenshteinDistance levenshtein;

    public void addNewBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Long id) {
        if(bookRepository.existsById(id)) {
            bookRepository.save(
                    bookRepository.getReferenceById(id)
            );
        }
    }

    public Optional<Book> getBookById(Long id) {
        return Optional.of(bookRepository.getReferenceById(id));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksOfSuchGenre(GenreType genre) {
        List<Book> books = this.bookRepository.findAll();

        List<Book> neededBooks = new ArrayList<>();
        for(Book book : books) {
            if(book.getGenre().equals(genre)) {
                neededBooks.add(book);
            }
        }

        return neededBooks;
    }

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
            }

            searchedDistance = levenshtein.apply(lowerCase, titleLowerCase);

            if(searchedDistance <= 4) {
                neededBooks.add(book);
            }
        }

        return neededBooks;
    }
}
