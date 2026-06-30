package org.jedi_bachelor.bookstatistic.analyzeservice.inbox.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jedi_bachelor.ioboxstarter.core.InboxMessage;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inbox_analyze")
@Data
public class InboxAnalyzeEntity extends InboxMessage {
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
