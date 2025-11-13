package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.Book;
import org.jedi_bachelor.model.enums.GenreType;
import org.jedi_bachelor.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    @Autowired
    private final BookService bookService;

    @GetMapping("/page/all")
    public String getAllBooksPage(Model model) {
        try {
            List<Book> books = this.bookService.getAllBooks();
            model.addAttribute("books", books);
            return "books/all-books";
        } catch(Exception ex) {
            model.addAttribute("error", "Не удалось загрузить список книг");
            return "error";
        }
    }

    @GetMapping("/page/{id}")
    public String getBookPage(@PathVariable Long id, Model model) {
        Optional<Book> book = this.bookService.getBookById(id);

        if(book.isEmpty()) {
            model.addAttribute("error", "Книга с ID " + id + " не найдена");
            return "error";
        } else {
            model.addAttribute("book", book.get());
            return "books/book-detail";
        }
    }

    @GetMapping("/page/genres/{genre}")
    public String getBooksByGenrePage(@PathVariable GenreType genre, Model model) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchGenre(genre);
            model.addAttribute("books", books);
            model.addAttribute("genre", genre);
            return "books/books-by-genre";
        } catch(Exception ex) {
            model.addAttribute("error", "Не удалось загрузить книги жанра " + genre);
            return "error";
        }
    }

    @GetMapping("/page/authors/{author}")
    public String getBooksByAuthorPage(@PathVariable String author, Model model) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchAuthor(author);
            model.addAttribute("books", books);
            model.addAttribute("author", author);
            return "books/books-by-author";
        } catch(Exception ex) {
            model.addAttribute("error", "Не удалось загрузить книги автора " + author);
            return "error";
        }
    }

    @GetMapping("/page/titles/{title}")
    public String getBooksByTitlePage(@PathVariable String title, Model model) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchTitle(title);
            model.addAttribute("books", books);
            model.addAttribute("title", title);
            return "books/books-by-title";
        } catch(Exception ex) {
            model.addAttribute("error", "Не удалось загрузить книги с названием " + title);
            return "error";
        }
    }

    @GetMapping("/page/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String getAddBookPage(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("genres", GenreType.values());
        model.addAttribute("isEdit", false);
        return "books/book-form";
    }

    @GetMapping("/page/edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String getEditBookPage(@PathVariable Long id, Model model) {
        Optional<Book> book = this.bookService.getBookById(id);

        if(book.isEmpty()) {
            model.addAttribute("error", "Книга для редактирования не найдена");
            return "error";
        } else {
            model.addAttribute("book", book.get());
            model.addAttribute("genres", GenreType.values());
            model.addAttribute("isEdit", true);
            return "books/book-form";
        }
    }

    // Обработка добавления книги
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addNewBook(@ModelAttribute Book book) {
        bookService.addNewBook(book);
        return "redirect:/books/page/all";
    }

    // Обработка обновления книги
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String updateBook(@ModelAttribute Book book) {
        bookService.updateBook(book);
        return "redirect:/books/page/" + book.getId();
    }

    @PostMapping("/api/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<String> addNewBookApi(@RequestBody Book book) {
        bookService.addNewBook(book);
        return ResponseEntity.ok("Книга успешно добавлена");
    }

    @PatchMapping("/api/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<String> updateBookApi(@RequestBody Book book) {
        bookService.updateBook(book);
        return ResponseEntity.ok("Книга успешно обновлена");
    }

    @GetMapping("/api/get/{id}")
    @ResponseBody
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = this.bookService.getBookById(id);

        if(book.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(book.get());
        }
    }

    @GetMapping("/api/get")
    @ResponseBody
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            List<Book> books = this.bookService.getAllBooks();
            return ResponseEntity.ok(books);
        } catch(Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/get/genres/{genre}")
    @ResponseBody
    public ResponseEntity<List<Book>> getBooksOfSuchGenre(@PathVariable GenreType genre) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchGenre(genre);
            return ResponseEntity.ok(books);
        } catch(Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/get/authors/{author}")
    @ResponseBody
    public ResponseEntity<List<Book>> getBooksOfSuchAuthor(@PathVariable String author) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchAuthor(author);
            return ResponseEntity.ok(books);
        } catch(Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/get/titles/{title}")
    @ResponseBody
    public ResponseEntity<List<Book>> getBooksOfSuchTitle(@PathVariable String title) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchTitle(title);
            return ResponseEntity.ok(books);
        } catch(Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}