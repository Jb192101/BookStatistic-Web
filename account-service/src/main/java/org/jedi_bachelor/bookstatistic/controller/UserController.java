package org.jedi_bachelor.bookstatistic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Контроллер пользователей", description = "Для работы с пользователями")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Получение данных пользователя",
            description = "Получение данных пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно получен",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователя с таким ID не существует",
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
    public ResponseEntity<?> findUserById(
            @Parameter(
                    description = "Уникальный идентификатор пользователя в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID пользователя"
                    )
            ) @PathVariable UUID id) throws UserNotFoundException {
        UserDto user = this.userService.findUserById(id);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.FOUND.value(), user));
    }

    @GetMapping
    @Operation(summary = "Получение данных всех пользователей",
            description = "Получение данных всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно получены",
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
    public ResponseEntity<?> findAll() {
        List<UserDto> dtos = this.userService.findAll();

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.FOUND.value(), dtos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление пользователя",
            description = "Удаление пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно удалён",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователя с таким ID не существует",
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
    public ResponseEntity<?> deleteUserById(
            @Parameter(
                    description = "Уникальный идентификатор пользователя в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID пользователя"
                    )
            ) @PathVariable UUID id) throws UserNotFoundException {
        UserDto dto = this.userService.deleteUser(id);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление данных пользователя",
            description = "Обновление данных пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно обновлён",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователя с таким ID не существует",
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
    public ResponseEntity<?> updateUser(@RequestBody UserDto dto) throws UserNotFoundException {
        UserDto user = this.userService.updateUser(dto);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), user));
    }

    @GetMapping("/emails")
    @Operation(summary = "Получение email пользователей",
            description = "Получение email пользователей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email адреса успешно получены",
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
    public ResponseEntity<?> getEmailAddresses() {
        List<String> addresses = this.userService.getEmailAddresses();

        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), addresses));
    }
}
