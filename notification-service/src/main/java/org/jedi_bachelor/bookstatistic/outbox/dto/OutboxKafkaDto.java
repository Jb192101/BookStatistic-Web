package org.jedi_bachelor.bookstatistic.outbox.dto;

public record OutboxKafkaDto(
        Long id,
        String title,
        String messageResult
) {
}
