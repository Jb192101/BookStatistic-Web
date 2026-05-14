package org.jedi_bachelor.bookstatistic.bookservice.inbox;

public record OutboxKafkaMessage(
        Long id,
        String title,
        String messageResult
) {
}
