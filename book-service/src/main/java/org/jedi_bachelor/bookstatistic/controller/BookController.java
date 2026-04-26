package org.jedi_bachelor.bookstatistic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.dto.mapentities.TextDto;
import org.jedi_bachelor.bookstatistic.dto.request.book.BookCreationDto;
import org.jedi_bachelor.bookstatistic.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.exceptions.BookNotFoundException;
import org.jedi_bachelor.bookstatistic.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Контроллер работы с книгами", description = "Для работы с книгами")
public class BookController {
    private final BookService bookService;

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
    public ResponseEntity<?> addBookWithoutText(@RequestBody BookCreationDto dto) {
        BookDto bookDto = this.bookService.addBookWithoutText(dto);

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
                new SuccessResponse(201, bookDto)
        );
    }

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
    public ResponseEntity<?> linkTextToBook(@PathVariable UUID bookId, @RequestBody TextDto dto) throws BookNotFoundException {
        this.bookService.linkTextToBook(bookId, dto);

        return ResponseEntity.ok(
                new SuccessResponse(200, Map.of("linked", true)));
    }

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
            ) @PathVariable UUID bookId) throws BookNotFoundException {
        BookDto bookDto = this.bookService.getBookById(bookId);

        return ResponseEntity.ok(new SuccessResponse(200, bookDto));
    }

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

        return ResponseEntity.ok(new SuccessResponse(200, bookDtoList));
    }

    @GetMapping("/{bookId}/text")
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
            ) @PathVariable UUID bookId) {
        return null;
    }
}
