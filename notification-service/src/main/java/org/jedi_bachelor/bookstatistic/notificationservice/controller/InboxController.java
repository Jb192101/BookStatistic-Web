package org.jedi_bachelor.bookstatistic.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.notification.InboxNotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.InboxContentManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notification/inbox")
public class InboxController {
    private final InboxContentManager inboxContentManager;

    @GetMapping
    @Operation(
            summary = "Получение всех inbox-сообщений",
            description = "Получаем список всех inbox-сообщений"
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
    public ResponseEntity<?> getAllInboxMessages() {
        List<InboxNotificationDto> dtos = this.inboxContentManager.findAllInboxNotifications();

        return ResponseEntity.status(200).body(
                dtos
        );
    }
}
