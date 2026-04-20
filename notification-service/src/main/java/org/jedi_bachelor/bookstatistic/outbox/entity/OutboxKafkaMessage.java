package org.jedi_bachelor.bookstatistic.outbox.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Data;
import org.jedi_bachelor.bookstatistic.outbox.interfaces.OutboxEnable;
import org.jedi_bachelor.bookstatistic.outbox.listener.OutboxListener;

@Entity
@EntityListeners(OutboxListener.class)
@Table(name = "outbox-kafka")
@Data
public class OutboxKafkaMessage implements OutboxEnable {
}
