package org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification;

import java.util.UUID;

public record NotificationSettingsCreatingDto(
        UUID userId,
        Boolean enableEmail,
        String emailAddress
) {
}
