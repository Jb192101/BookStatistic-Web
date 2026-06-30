package org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NotificationCreationDto (
        @NotNull
        UUID userId,

        @NotNull
        String type,

        @NotNull
        String notificationTitle,

        @NotNull
        String message
) {
}
