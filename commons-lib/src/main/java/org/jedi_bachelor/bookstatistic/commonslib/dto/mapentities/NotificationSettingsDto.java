package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.util.UUID;

public record NotificationSettingsDto(
    UUID id,
    UUID userId,
    boolean enableEmail,
    boolean enableGettingBroadcastMessages,
    String email,
    String telegramAddress
) {
}
