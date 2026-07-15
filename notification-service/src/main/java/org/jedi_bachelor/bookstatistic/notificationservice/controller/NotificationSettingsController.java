package org.jedi_bachelor.bookstatistic.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationSettingsDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsCreatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsUpdateDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserHaventAccessException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.notificationservice.service.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/notification-setting")
public class NotificationSettingsController {
    private final NotificationService notificationService;

    // ROLE_ADMIN, ROLE_MODERATOR
    @PostMapping
    @Operation(summary = "Добавление настроек уведомлений пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное добавление настроек пользователя"
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
    public ResponseEntity<?> addNotificationSettings(@RequestBody NotificationSettingsCreatingDto dto) throws NotificationSettingsNotExistsException {
        this.notificationService.addNotificationSettings(dto);

        return ResponseEntity.status(201).body(null);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/{userId}")
    @Operation(summary = "Получение настроек уведомлений пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление уведомления пользователя"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Уведомления с таким ID нет",
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
    public ResponseEntity<?> getNotificationSettings(@Parameter(
            description = "Уникальный идентификатор пользователя в формате UUID",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440001",
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    description = "UUID пользователя"
            )
    ) @PathVariable UUID userId) throws UserNotFoundException, UserHaventAccessException {
        NotificationSettingsDto dto = this.notificationService.getNotificationSettings(userId);

        return ResponseEntity.ok(dto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @DeleteMapping("/{userId}")
    @Operation(summary = "Удалить настройку уведомлений пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление уведомления пользователя"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Уведомления с таким ID нет",
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
    public ResponseEntity<?> deleteNotificationSettings(@Parameter(
            description = "Уникальный идентификатор пользователя в формате UUID",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440001",
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    description = "UUID пользователя"
            )
    ) @PathVariable UUID userId) throws UserNotFoundException, UserHaventAccessException {
        this.notificationService.deleteNotificationSettings(userId);

        return ResponseEntity.ok(null);
    }

    // Протестировать
    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @PatchMapping("/{userId}")
    @Operation(summary = "Обновить настройку уведомлений пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное удаление уведомления пользователя"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Уведомления с таким ID нет",
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
    public ResponseEntity<?> updateNotificationSettings(@Parameter(
            description = "Уникальный идентификатор пользователя в формате UUID",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440001",
            schema = @Schema(
                    type = "string",
                    format = "uuid",
                    description = "UUID пользователя"
            )
    ) @PathVariable UUID userId, @RequestBody NotificationSettingsUpdateDto dto) throws UserNotFoundException, UserHaventAccessException {
        NotificationSettingsDto saved = this.notificationService.updateNotificationSettings(userId, dto);

        return ResponseEntity.ok(saved);
    }
}
