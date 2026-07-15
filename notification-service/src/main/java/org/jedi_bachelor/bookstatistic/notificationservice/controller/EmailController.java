package org.jedi_bachelor.bookstatistic.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.BroadcastMessage;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.notificationservice.service.EmailService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/email")
@Slf4j
public class EmailController {
    private final EmailService emailService;

    // ROLE_ADMIN, ROLE_MODERATOR
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
    public ResponseEntity<?> sendBroadcastMessage(@RequestBody BroadcastMessage message) {
        log.info("Received broadcast: subject='{}', message='{}'",
                message.subject(), message.message());

        this.emailService.sendBroadcastMessage(message);

        return ResponseEntity.ok(true);
    }
}
