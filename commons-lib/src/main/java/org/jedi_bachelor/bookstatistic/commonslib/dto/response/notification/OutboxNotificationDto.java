package org.jedi_bachelor.bookstatistic.commonslib.dto.response.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record OutboxNotificationDto(
        Long id, UUID notificationId, String title, String messageResult,
        Boolean published, LocalDateTime publishedAt, LocalDateTime createdAt,
        Integer retryCount
) {
}
