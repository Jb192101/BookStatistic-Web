package org.jedi_bachelor.bookstatistic.notificationservice.outbox.dto;

public record OutboxKafkaDto(
        Long id,
        String title,
        String messageResult
) {
}
