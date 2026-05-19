package org.jedi_bachelor.bookstatistic.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsCreatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.notificationservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Контроллер уведомлений", description = "Для работы с уведомления")
public class NotificationController {
    private final NotificationService notificationService;

    @RolesAllowed("ADMIN")
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

    @RolesAllowed({ "ADMIN", "USER" })
    @GetMapping
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
            ) @PathVariable UUID notificationId) throws NotificationNotFoundException {
        NotificationDto dto = this.notificationService.getNotification(notificationId);

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                dto
        ));
    }

    @RolesAllowed({ "ADMIN", "USER" })
    @GetMapping("/{userId}")
    @Operation(summary = "Получение уведомлений пользователя",
            description = "Получение всех уведомлений пользователя по его ID")
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
            ) @PathVariable UUID userId) throws UserNotFoundException {
        List<NotificationDto> dtos = this.notificationService.getUserNotifications(userId);

        return ResponseEntity.ok(new SuccessResponse(200, dtos));
    }

    @RolesAllowed({ "ADMIN", "USER" })
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
    public ResponseEntity<?> addNewNotification(@RequestBody NotificationCreationDto dto) {
        NotificationDto notificationDto = this.notificationService.addNewNotification(dto);

        return ResponseEntity.status(201).body(new SuccessResponse(
                HttpStatus.CREATED.value(),
                notificationDto
        ));
    }

    @RolesAllowed({ "ADMIN", "USER" })
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
                            description = "UUID пользователя"
                    )
            ) @PathVariable UUID notificationId) throws NotificationNotFoundException {
        this.notificationService.deleteNotification(notificationId);

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                Map.of("deleted", true)
        ));
    }

    @PostMapping("/notification-settings/{userId}")
    public ResponseEntity<?> addNotificationSettings(@PathVariable NotificationSettingsCreatingDto dto) {
        this.notificationService.addNotificationSettings(dto);

        return ResponseEntity.ok(new SuccessResponse(200, null));
    }

    @DeleteMapping("/notification-settings/{userId}")
    public ResponseEntity<?> deleteNotificationSettings(@PathVariable UUID userId) throws UserNotFoundException {
        this.notificationService.deleteNotificationSettings(userId);

        return ResponseEntity.ok(new SuccessResponse(200, null));
    }
}
