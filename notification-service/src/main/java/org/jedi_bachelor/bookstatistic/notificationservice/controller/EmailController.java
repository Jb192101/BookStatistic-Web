package org.jedi_bachelor.bookstatistic.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.BroadcastMessage;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.notificationservice.service.EmailService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Класс для рассылок сообщений по почте
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/email")
@Slf4j
@Tag(name = "Контроллер email-сообщений", description = "Для отправки широковещательных (broadcast) сообщений по почте")
public class EmailController {
    private final EmailService emailService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/broadcast")
    @Operation(summary = "Отправка broadcast-сообщения",
            description = "Отправляет broadcast-сообщение всем, кто пожелал получать уведомления по почте")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная отправка сообщения",
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
    public ResponseEntity<?> sendBroadcastMessage(BroadcastMessage message) {
        this.emailService.sendBroadcastMessage(message);

        return ResponseEntity.ok(true);
    }
}
