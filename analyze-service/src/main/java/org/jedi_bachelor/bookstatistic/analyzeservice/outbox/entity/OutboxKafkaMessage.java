package org.jedi_bachelor.bookstatistic.analyzeservice.outbox.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "outbox_kafka_messages")
public class OutboxKafkaMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
