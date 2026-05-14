package org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification;

import java.util.UUID;

public record NotificationCreationDto (
UUID userId, String type, String notificationTitle, String message
) {
}
