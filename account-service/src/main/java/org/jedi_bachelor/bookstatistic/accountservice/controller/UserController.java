package org.jedi_bachelor.bookstatistic.accountservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.service.UserService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.UserUpdateDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.PasswordInvalidException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UsernameAlreadyExistsException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "Контроллер пользователей", description = "Для работы с пользователями")
public class UserController {
    private final UserService userService;

    // ROLE_ADMIN
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> dtos = this.userService.getAllProfiles();

        return ResponseEntity.ok(dtos);
    }

    // ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) throws UserNotFoundException {
        UserDto profile = this.userService.getUserById(userId);

        return ResponseEntity.ok(profile);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
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

        return ResponseEntity.ok().body(dto);
    }

    // ROLE_ADMIN, ROLE_USER, ROLE_MODERATOR
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
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UserUpdateDto dto) throws UserNotFoundException, UsernameAlreadyExistsException, PasswordInvalidException {
        UserDto user = this.userService.updateUser(id, dto);

        return ResponseEntity.ok().body(user);
    }
}
