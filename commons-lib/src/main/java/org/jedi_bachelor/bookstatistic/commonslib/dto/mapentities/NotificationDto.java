package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.util.UUID;

public record NotificationDto(
    UUID id,
    UUID userId,
    String type,
    String notificationTitle,
    String message
) {
}
