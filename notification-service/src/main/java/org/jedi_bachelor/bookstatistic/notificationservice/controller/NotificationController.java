package org.jedi_bachelor.bookstatistic.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationSettingsDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsCreatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserHaventAccessException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.notificationservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Контроллер уведомлений", description = "Для работы с уведомления")
public class NotificationController {
    private final NotificationService notificationService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @Operation(summary = "Получение всех уведомлений",
            description = "Получение всех уведомлений всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение всех уведомлений",
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
    public ResponseEntity<?> getAllNotifications() {
        List<NotificationDto> dtos = this.notificationService.getAllNotifications();

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                dtos
        ));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{notificationId}")
    @Operation(summary = "Получение уведомления",
            description = "Получение конкретного уведомления по его ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Уведомление успешно получено",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Уведомление не найдено",
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
    public ResponseEntity<?> getNotification(
            @Parameter(
                    description = "Уникальный идентификатор уведомления в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID пользователя"
                    )
            ) @PathVariable UUID notificationId) throws NotificationNotFoundException, UserHaventAccessException {
        NotificationDto dto = this.notificationService.getNotification(notificationId);

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                dto
        ));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/system/user/{userId}")
    @Operation(summary = "Получение системных уведомлений пользователя",
            description = "Получение всех системных уведомлений пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Уведомления успешно получены",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
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
    public ResponseEntity<?> getUserNotificationsInSystem(
            @Parameter(
                    description = "Уникальный идентификатор пользователя в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID пользователя"
                    )
            ) @PathVariable UUID userId) throws UserNotFoundException, UserHaventAccessException {
        List<NotificationDto> dtos = this.notificationService.getUserNotificationsInSystem(userId);

        return ResponseEntity.ok(new SuccessResponse(200, dtos));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/{userId}")
    @Operation(summary = "Получение уведомлений пользователя",
            description = "Получение всех уведомлений пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Уведомления успешно получены",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
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
    public ResponseEntity<?> getUserNotifications(
            @Parameter(
                    description = "Уникальный идентификатор пользователя в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID пользователя"
                    )
            ) @PathVariable UUID userId) throws UserNotFoundException, UserHaventAccessException {
        List<NotificationDto> dtos = this.notificationService.getUserNotifications(userId);

        return ResponseEntity.ok(new SuccessResponse(200, dtos));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping
    @Operation(
            summary = "Создать уведомление",
            description = "Создает новое уведомление для пользователя на основе переданного DTO"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Уведомление успешно создано",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
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
    public ResponseEntity<?> addNewNotification(@RequestBody NotificationCreationDto dto) throws NotificationSettingsNotExistsException {
        NotificationDto notificationDto = this.notificationService.addNewNotification(dto);

        return ResponseEntity.status(201).body(new SuccessResponse(
                HttpStatus.CREATED.value(),
                notificationDto
        ));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Удалить уведомление пользователя по ID")
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
    public ResponseEntity<?> deleteNotification(
            @Parameter(
                    description = "Уникальный идентификатор уведомления в формате UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001",
                    schema = @Schema(
                            type = "string",
                            format = "uuid",
                            description = "UUID уведомления"
                    )
            ) @PathVariable UUID notificationId) throws NotificationNotFoundException, UserHaventAccessException {
        this.notificationService.deleteNotification(notificationId);

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                Map.of("deleted", true)
        ));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/notification-settings")
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
    public ResponseEntity<?> addNotificationSettings(@RequestBody NotificationSettingsCreatingDto dto) {
        this.notificationService.addNotificationSettings(dto);

        return ResponseEntity.ok(new SuccessResponse(201, null));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/notification-settings/{userId}")
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

        return ResponseEntity.ok(new SuccessResponse(200, dto));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @DeleteMapping("/notification-settings/{userId}")
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

        return ResponseEntity.ok(new SuccessResponse(200, null));
    }
}
