package org.jedi_bachelor.bookstatistic.bookservice.audit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.jedi_bachelor.bookstatistic.bookservice.audit.AuditRevisionListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "revision_info")
@Getter
@Setter
@NoArgsConstructor
@RevisionEntity(AuditRevisionListener.class)
public class AuditRevisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long revisionId;

    @RevisionTimestamp
    @Column(nullable = false)
    private LocalDateTime revisionTimestamp = LocalDateTime.now();

    @Column(nullable = false)
    private String username;

    @Column
    private String userAgent;
}
