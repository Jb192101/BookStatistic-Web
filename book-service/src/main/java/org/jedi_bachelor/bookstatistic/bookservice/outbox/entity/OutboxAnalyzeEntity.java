package org.jedi_bachelor.bookstatistic.bookservice.outbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;

import java.util.UUID;

@Entity
@Table(name = "outbox_analyze")
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboxAnalyzeEntity extends OutboxMessage {
    @Column(name = "book_id")
    private UUID bookId;

    @Column(name = "filename")
    private String filename;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size")
    private Long size;
}
