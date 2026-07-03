package org.jedi_bachelor.bookstatistic.bookservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.book.OutboxAnalyzeDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.notification.OutboxNotificationDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/books/outbox")
@RequiredArgsConstructor
public class OutboxController {
    private final OutboxContentManager outboxContentManager;

    // ROLE_ADMIN, ROLE_MODERATOR
    @GetMapping
    @Operation(
            summary = "Получение всех outbox-сообщений",
            description = "Получаем список всех outbox-сообщений"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Все сообщения получены успешно",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationDto.class)
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
    public ResponseEntity<?> getAllOutboxMessages() {
        List<OutboxAnalyzeDto> dtos = this.outboxContentManager.findAllOutboxAnalyzeMessages();

        return ResponseEntity.status(200).body(
                dtos
        );
    }
}
