package org.jedi_bachelor.bookstatistic.notificationservice.outbox.dto;

import java.util.UUID;

public record OutboxKafkaDto(
        Long id,
        UUID notificationId,
        String title,
        String messageResult
) {
}
