package org.jedi_bachelor.bookstatistic.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotifications() {

    }

    @GetMapping
    public ResponseEntity<NotificationDto> getNotification(@PathVariable UUID notificationId) {

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
        this.notificationService.addNewNotification(dto);

        return ResponseEntity.of(new SuccessResponse());
    }
}
