package org.jedi_bachelor.bookstatistic.notificationservice.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SYSTEM_ONLY(true, false),
    EMAIL_ONLY(false, true),
    EMAIL_AND_SYSTEM(true, true);

    private final boolean isSystem;
    private final boolean isEmail;
}