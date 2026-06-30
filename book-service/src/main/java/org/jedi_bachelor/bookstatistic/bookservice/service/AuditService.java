package org.jedi_bachelor.bookstatistic.bookservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.jedi_bachelor.bookstatistic.bookservice.audit.entity.AuditRevisionEntity;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Book;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.audit.AuditRevisionDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Получить всю историю изменений книг
     * @param desc - выводить в обратном порядке или нет
     */
    @Transactional
    public List<AuditRevisionDto> getUserAuditHistory(boolean desc) {
        AuditReader auditReader = AuditReaderFactory.get(this.entityManager);

        List<Object[]> revisions = (List<Object[]>) auditReader.createQuery()
                .forRevisionsOfEntity(Book.class, false, true)
                .getResultList();

        List<AuditRevisionDto> dtos = new ArrayList<>();

        for (Object[] revision : revisions) {
            Book book = (Book) revision[0];
            Object revisionEntity = revision[1];
            RevisionType revisionType = (RevisionType) revision[2];  // ← RevisionType, а не Integer!

            AuditRevisionDto.AuditRevisionDtoBuilder builder = AuditRevisionDto.builder();

            // 1. Информация о книге
            if (book != null) {
                builder.entityId(book.getId().toString());
                builder.entityType("Book");
                builder.entitySnapshot(String.format(
                        "{\"id\":\"%s\",\"title\":\"%s\",\"description\":\"%s\",\"pages\":%d}",
                        book.getId(),
                        book.getTitle() != null ? book.getTitle() : "",
                        book.getDescription() != null ? book.getDescription() : "",
                        book.getPages() != null ? book.getPages() : 0
                ));
            }

            // 2. Информация о ревизии (кто, когда, User-Agent)
            if (revisionEntity instanceof AuditRevisionEntity auditRev) {
                builder.revisionId(auditRev.getRevisionId());
                builder.revisionTimestamp(auditRev.getRevisionTimestamp());
                builder.username(auditRev.getUsername() != null ? auditRev.getUsername() : "anonymous");
                builder.userAgent(auditRev.getUserAgent() != null ? auditRev.getUserAgent() : "unknown");

            } else if (revisionEntity instanceof DefaultRevisionEntity defaultRev) {
                builder.revisionId((long) defaultRev.getId());
                builder.revisionTimestamp(LocalDateTime.ofInstant(
                        defaultRev.getRevisionDate().toInstant(),
                        ZoneId.systemDefault()
                ));
                builder.username("anonymous");
                builder.userAgent("unknown");
            }

            // 3. Тип изменения (используем RevisionType)
            builder.revisionType(getRevisionTypeName(revisionType));

            dtos.add(builder.build());
        }

        if (desc) {
            Collections.reverse(dtos);
        }

        log.info("Found {} audit records", dtos.size());
        return dtos;
    }

    /**
     * Преобразование числового типа ревизии в строковое представление
     */
    private String getRevisionTypeName(RevisionType type) {
        if (type == null) return "UNKNOWN";
        return switch (type) {
            case ADD -> "ADD";
            case MOD -> "MOD";
            case DEL -> "DEL";
            default -> "UNKNOWN";
        };
    }
}