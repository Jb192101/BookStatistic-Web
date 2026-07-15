package org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BroadcastMessage(
        @NotNull
        @Min(value = 1)
        String subject,

        @NotNull
        @Min(value = 1)
        String message
) {
}
