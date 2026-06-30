package org.jedi_bachelor.bookstatistic.analyzeservice.outbox.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "outbox_analyze_result")
@Data
public class OutboxAnalyzeResultEntity extends OutboxMessage {
}
