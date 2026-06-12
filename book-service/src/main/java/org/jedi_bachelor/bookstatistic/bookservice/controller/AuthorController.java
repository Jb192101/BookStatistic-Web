package org.jedi_bachelor.bookstatistic.bookservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.service.AuthorService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.AuthorDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookAuthorRelationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.AuthorCreationUpdatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookAuthorRelationKey;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.LinkBookToAuthorTaskDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/v1/authors")
@RequiredArgsConstructor
@Tag(name = "Контроллер работы с авторами", description = "Для работы с авторами")
public class AuthorController {
    private final AuthorService authorService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "Добавление автора",
            description = "Добавление автора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Успешное добавление нового автора",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Автор уже есть в системе",
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
    public ResponseEntity<?> addNewAuthor(@RequestBody AuthorCreationUpdatingDto dto) throws AuthorAlreadyExistsException {
        AuthorDto author = this.authorService.addNewAuthor(dto);

        return ResponseEntity.status(201).body(author);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{authorId}")
    @Operation(summary = "Удаление автора",
            description = "Удаление автора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление автора",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Автора нет в системе",
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
    public ResponseEntity<?> deleteAuthor(@PathVariable UUID authorId) throws AuthorNotFoundException {
        this.authorService.deleteAuthor(authorId);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{authorId}")
    @Operation(summary = "Обновление данных автора",
            description = "Обновление данных автора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное обновление данных автора",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Автора нет в системе",
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
    public ResponseEntity<?> updateAuthor(@PathVariable UUID authorId,
                                          @RequestBody AuthorCreationUpdatingDto dto)
            throws AuthorNotFoundException {
        AuthorDto authorDto = this.authorService.updateAuthor(authorId, dto);

        return ResponseEntity.ok(authorDto);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/{authorId}")
    @Operation(summary = "Поиск автора",
            description = "Поиск автора по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное нахождение автора",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Автора нет в системе",
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
    public ResponseEntity<?> findAuthorById(UUID authorId) throws AuthorNotFoundException {
        AuthorDto authorDto = this.authorService.getAuthorById(authorId);

        return ResponseEntity.ok(authorDto);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/{authorId}")
    @Operation(summary = "Нахождение всех авторов",
            description = "Нахождение всех авторов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение всех авторов",
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
    public ResponseEntity<?> findAllAuthors() {
        List<AuthorDto> authorDtoList = this.authorService.getAllAuthors();

        return ResponseEntity.ok(authorDtoList);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/book-relation")
    @Operation(summary = "Привязка отношения",
            description = "Привязка отношения книга-автор")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Успешное добавление нового отношения",
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
    public ResponseEntity<?> linkBookToAuthor(@RequestBody LinkBookToAuthorTaskDto dto) throws AuthorNotFoundException, BookNotFoundException, BookAuthorRelationAlreadyExistsException {
        BookAuthorRelationDto relation = this.authorService.linkBookToAuthor(dto);

        return ResponseEntity.status(201).body(relation);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/book-relation")
    @Operation(summary = "Удаление отношения",
            description = "Удаления отношения книга-автор")
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
    public ResponseEntity<?> deleteBookAuthorRelation(@RequestBody BookAuthorRelationKey key) throws BookAuthorRelationNotFoundException {
        this.authorService.deleteBookAuthorRelation(key);

        return ResponseEntity.ok().build();
    }
}
