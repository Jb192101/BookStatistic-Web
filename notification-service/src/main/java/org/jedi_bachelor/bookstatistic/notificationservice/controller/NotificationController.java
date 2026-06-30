package org.jedi_bachelor.bookstatistic.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.notificationservice.service.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

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

        return ResponseEntity.ok().body(
                dtos
        );
    }

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

        return ResponseEntity.status(201).body(
                notificationDto
        );
    }
}
