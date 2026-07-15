package org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NotificationSettingsCreatingDto(
        @NotNull
        UUID userId,

        @NotNull
        Boolean enableEmail,

        @NotNull
        @Email
        String emailAddress,

        String telegram,

        @NotNull
        Boolean enableBroadcastMessages
) {
}
