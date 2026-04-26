package org.jedi_bachelor.bookstatistic.inbox;

public record OutboxKafkaMessage(
        Long id,
        String title,
        String messageResult
) {
}
