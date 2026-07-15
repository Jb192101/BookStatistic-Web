package org.jedi_bachelor.bookstatistic.commonslib.dto.response.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record InboxNotificationDto(
        Long id, UUID userId, String type, String notificationTitle, String message,
        LocalDateTime processedAt, int retryCount, LocalDateTime createdAt, Boolean processed
) {
}
