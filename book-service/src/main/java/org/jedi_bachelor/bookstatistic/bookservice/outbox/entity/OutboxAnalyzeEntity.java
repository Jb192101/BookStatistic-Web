package org.jedi_bachelor.bookstatistic.bookservice.outbox.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;

@Entity
@Table(name = "outbox_analyze")
@Data
public class OutboxAnalyzeEntity {
    private KafkaTextAnalyzeDto dto;
}
