package org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification;

public record NotificationSettingsUpdateDto(
        Boolean enableEmail,
        String emailAddress,
        String telegram,
        Boolean enableBroadcastMessages
) {
}
