package org.jedi_bachelor.bookstatistic.analyzeservice.outbox.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_kafka_messages")
public class OutboxKafkaMessage {
}
