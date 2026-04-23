package org.jedi_bachelor.bookstatistic.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getAllNotifications() {
        List<NotificationDto> dtos = this.notificationService.getAllNotifications();

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                dtos
        ));
    }

    @GetMapping
    public ResponseEntity<?> getNotification(@PathVariable UUID notificationId) {
        NotificationDto dto = this.notificationService.getNotification(notificationId);

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                dto
        ));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable UUID userId) {
        return ResponseEntity.of(
                Optional.ofNullable(
                        this.notificationService.getUserNotifications(userId)
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> addNewNotification(@RequestBody NotificationCreationDto dto) {
        NotificationDto notificationDto = this.notificationService.addNewNotification(dto);

        return ResponseEntity.status(201).body(new SuccessResponse(
                HttpStatus.CREATED.value(),
                notificationDto
        ));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable UUID notificationId) {
        this.notificationService.deleteNotification(notificationId);

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                Map.of("deleted", true)
        ));
    }

    /**
     * Эндпоинт ТОЛЬКО для создания новых настроек
     * @param userId
     * @return
     */
    @PostMapping("/settings/{userId}")
    public ResponseEntity<?> addNewNotificationSettings(@PathVariable UUID userId) {
        this.notificationService.addNotificationSettings(userId);

        return ResponseEntity.status(201).body(new SuccessResponse(
                HttpStatus.CREATED.value(),
                Map.of("created", true)
        ));
    }

    @DeleteMapping("/settings/{userId}")
    public ResponseEntity<?> deleteNotificationSettings(@PathVariable UUID userId) {
        this.notificationService.deleteNotificationSettings(userId);

        return ResponseEntity.ok().body(new SuccessResponse(
                HttpStatus.OK.value(),
                Map.of("deleted", true)
        ));
    }
}
