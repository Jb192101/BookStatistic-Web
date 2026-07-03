package org.jedi_bachelor.bookstatistic.bookservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity.TextFile;
import org.jedi_bachelor.bookstatistic.bookservice.service.BookService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookAuthorRelationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserBookRelationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookCreationUpdatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.UserBookRelationCreatingUpdatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookIdEntity;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.book.UserReadingStat;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
@Tag(name = "Контроллер работы с книгами", description = "Для работы с книгами")
public class BookController {
    private final BookService bookService;

    // ROLE_ADMIN, ROLE_MODERATOR
    @PostMapping
    @Operation(summary = "Добавление книги без текста",
            description = "Добавление книги без текста")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное добавление новой книги",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> addBookWithoutText(
            @RequestBody BookCreationUpdatingDto dto
    ) {
        BookDto bookDto = this.bookService.addBookWithoutText(dto);

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(bookDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @GetMapping("/{bookId}")
    @Operation(summary = "Получение книги по ID",
            description = "Получение книги по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение книги",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга с таким ID не найдена",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getBookByBookId(
            @Parameter(
                    description = "Уникальный идентификатор книги в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID книги"
                    )
            ) @PathVariable UUID bookId,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale
    ) throws BookNotFoundException {
        BookDto bookDto = this.bookService.getBookById(bookId);

        return ResponseEntity.ok(bookDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @PutMapping("/{bookId}")
    @Operation(summary = "Обновление книги",
            description = "Обновление данных в сущности Book")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение всех книг",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> updateBook(@PathVariable UUID bookId,
                                        @RequestBody BookCreationUpdatingDto dto) throws BookNotFoundException {
        BookDto bookDto = this.bookService.updateBookData(bookId, dto);

        return ResponseEntity.ok(bookDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping
    @Operation(summary = "Получение всех книг",
            description = "Получение всех книг всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение всех книг",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getAllBooks() {
        List<BookDto> bookDtoList = this.bookService.getAllBooks();

        return ResponseEntity.ok(bookDtoList);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/{userId}/statistics")
    @Operation(summary = "Получение статистики по чтению у пользователя",
            description = "Получение краткой статистики по пользователю. В частности получение:" +
                    "\n1. Кол-ва всех прочитанных книг." +
                    "\n2. Кол-во частично прочитанных книг." +
                    "\n3. Кол-во брошенных книг." +
                    "\n4. Кол-во не открытых книг.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение статистики",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getBookStatisticOfUser(@PathVariable UUID userId) {
        UserReadingStat userReadingStat = this.bookService.getReadingStatsByUserId(userId);

        return ResponseEntity.ok(userReadingStat);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @PatchMapping("/{bookId}")
    @Operation(summary = "Привязка текста к книге",
            description = "Привязка текста к книге")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение всех книг",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга с таким ID не найдена",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> linkTextToBook(
            @PathVariable UUID bookId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale
    ) throws BookNotFoundException, IOException, TextAlreadyLinkedException, TextNotFoundException {
        this.bookService.linkTextToBook(bookId, file);

        return ResponseEntity.ok(Map.of("linked", true));
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/{bookId}/text")
    @Operation(summary = "Выдача текста книги",
            description = "Выдача текста книги")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение текста книги",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга с таким ID не найдена",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getBookText(
            @Parameter(
                    description = "Уникальный идентификатор книги в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID книги"
                    )
            ) @PathVariable UUID bookId) throws BookNotFoundException, TextNotFoundException {
        TextFile file = this.bookService.getBookTextById(bookId);

        return ResponseEntity.ok(file);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @GetMapping("/texts")
    @Operation(summary = "Выдача текстов всех книг",
            description = "Выдача текстов всех книг")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение текстов",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getAllTexts() {
        List<TextFile> files = this.bookService.findAllTexts();

        return ResponseEntity.ok(files);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @DeleteMapping("/{bookId}")
    @Operation(summary = "Удаление книги",
            description = "Удаление книги")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление книги",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Книга с таким ID не найдена",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "405",
                    description = "Книга имеет привязанные аккаунты",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> deleteBookById(
            @Parameter(
                    description = "Уникальный идентификатор книги в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID книги"
                    )
            ) @PathVariable UUID bookId) throws BookNotFoundException {
        this.bookService.deleteBookById(bookId);

        return ResponseEntity.ok(Map.of("deleted", true));
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @PatchMapping("/{bookId}/text")
    @Operation(summary = "Обновление текста книги",
            description = "Обновление текста книги")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное обновление текста книги",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга с таким ID не найдена",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> updateBookText(
            @Parameter(
                    description = "Уникальный идентификатор книги в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID книги"
                    )
            ) @PathVariable UUID bookId,
            @RequestParam("file") MultipartFile file) throws BookNotFoundException, IOException {
        TextFile updatedFile = this.bookService.updateBookText(bookId, file);

        return ResponseEntity.ok(updatedFile);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/search")
    @Operation(summary = "Поиск книг",
            description = "Поиск книг по их названию или автору")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное обновление текста книги",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> searchBooksByTitleOrAuthor(
            @Parameter(
                    description = "Уникальный идентификатор книги в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID книги"
                    )
            ) @RequestParam String inputString) {
        List<BookDto> books = this.bookService.searchBooksByInputString(inputString);

        return ResponseEntity.ok(books);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @PostMapping("/reading")
    @Operation(summary = "Добавление отношения пользователь-книга",
            description = "Добавление отношения пользователь-книга")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное добавление новой связи",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книги с таким ID нет",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> addUserBookRelation(
            @RequestBody UserBookRelationCreatingUpdatingDto dto
    ) throws BookNotFoundException {
        UserBookRelationDto userBookDto = this.bookService.addUserBookRelation(dto);

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(userBookDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @PatchMapping("/reading")
    @Operation(summary = "Обновление отношения пользователь-книга",
            description = "Обновление отношения пользователь-книга")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное добавление новой связи",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книги с таким ID нет",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> updateUserBookRelation(
            @RequestBody UserBookRelationCreatingUpdatingDto dto
    ) throws BookNotFoundException, UserBookRelationNotFoundException {
        UserBookRelationDto userBookDto = this.bookService.updateUserBookRelation(dto);

        return ResponseEntity.ok(userBookDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @GetMapping("/reading/{userId}")
    @Operation(summary = "Получение отношения пользователь-книга",
            description = "Получение отношения пользователь-книга")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное добавление новой связи",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книги с таким ID нет",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getUserBookRelation(
            @PathVariable UUID userId, @RequestBody BookIdEntity key
    ) throws UserBookRelationNotFoundException {
        UserBookRelationDto userBookDto = this.bookService.getUserBookRelation(userId, key);

        return ResponseEntity.ok(userBookDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @GetMapping("/reading")
    @Operation(summary = "Получение всех отношений пользователь-книга",
            description = "Получение всех отношений пользователь-книга")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное добавление новой связи",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getAllUserBookRelations() {
        List<UserBookRelationDto> userBookDto = this.bookService.findAllUserBookRelations();

        return ResponseEntity.ok(userBookDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @DeleteMapping("/reading/{userId}")
    @Operation(summary = "Удаление отношения пользователь-книга",
            description = "Удаление отношения пользователь-книга")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное добавление новой связи",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книги с таким ID нет",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> deleteUserBookRelation(
            @PathVariable UUID userId, @RequestBody BookIdEntity key
    ) throws UserBookRelationNotFoundException {
        UserBookRelationDto userBookDto = this.bookService.deleteUserBookRelation(userId, key);

        return ResponseEntity.ok(userBookDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/book-relations/{bookId}")
    @Operation(summary = "Получение всех отношений конкретной книги",
            description = "Получение всех отношений книга-автор конкретной книги")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление отношения",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getBookAuthorRelationOfBook(
            @PathVariable UUID bookId
    ) {
        List<BookAuthorRelationDto> dtos = this.bookService.getBookAuthorRelationOfBook(bookId);

        return ResponseEntity.ok(dtos);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/user/{userId}")
    @Operation(summary = "Получение всех книг пользователя",
            description = "Получение всех книг, взятых пользователем себе в библиотеку")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление отношения",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getBooksOfUser(
            @PathVariable UUID userId
    ) {
        List<BookDto> dtos = this.bookService.getBooksOfUser(userId);

        return ResponseEntity.ok(dtos);
    }
}
