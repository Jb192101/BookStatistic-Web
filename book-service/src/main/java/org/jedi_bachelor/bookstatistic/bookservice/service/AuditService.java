package org.jedi_bachelor.bookstatistic.bookservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.jedi_bachelor.bookstatistic.bookservice.audit.entity.AuditRevisionEntity;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Book;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.audit.AuditRevisionDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
public class AuditService {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Получить всю историю изменений пользователя
     * @param desc - выводить в обратном порядке или нет
     */
    @Transactional
    public List<AuditRevisionDto> getUserAuditHistory(boolean desc) {
        AuditReader auditReader = AuditReaderFactory.get(this.entityManager);

        List<Object[]> revisions = (List<Object[]>) auditReader.createQuery()
                .forRevisionsOfEntity(
                        AuditRevisionEntity.class,
                        false,
                        true)
                .getResultList();

        List<AuditRevisionDto> dtos = this.convertRevisionsToEntities(revisions);

        if(desc) Collections.reverse(dtos);

        return dtos;
    }

    private List<AuditRevisionDto> convertRevisionsToEntities(List<Object[]> revisions) {
        if (revisions == null || revisions.isEmpty()) {
            return new ArrayList<>();
        }

        return revisions.stream()
                .map(this::convertRevisionToDto)
                .collect(Collectors.toList());
    }

    private AuditRevisionDto convertRevisionToDto(Object[] revision) {
        Object entity = revision[0];
        Object revisionEntity = revision[1];
        Integer revisionType = (Integer) revision[2];

        AuditRevisionDto.AuditRevisionDtoBuilder builder = AuditRevisionDto.builder();

        // Заполняем информацию о ревизии
        if (revisionEntity instanceof DefaultRevisionEntity defaultRev) {
            builder.revisionId((long) defaultRev.getId());
            builder.revisionTimestamp(LocalDateTime.from(defaultRev.getRevisionDate().toInstant()));
        } else if (revisionEntity instanceof AuditRevisionEntity auditRev) {
            builder.revisionId(auditRev.getRevisionId());
            builder.revisionTimestamp(auditRev.getRevisionTimestamp());
            builder.username(auditRev.getUsername());
            builder.userAgent(auditRev.getUserAgent());
        }

        // Заполняем тип изменения
        builder.revisionType(getRevisionTypeName(revisionType));

        // Заполняем информацию о сущности
        if (entity instanceof Book book) {
            builder.entityId(book.getId().toString());
            builder.entityType("Book");
            builder.entitySnapshot(String.format(
                    "{\"id\":\"%s\",\"title\":\"%s\",\"description\":\"%s\"}",
                    book.getId(), book.getTitle(), book.getDescription()
            ));
        }

        return builder.build();
    }

    /**
     * Преобразование числового типа ревизии в строковое представление
     */
    private String getRevisionTypeName(Integer type) {
        if (type == null) return "UNKNOWN";
        return switch (type) {
            case 0 -> "ADD";
            case 1 -> "MOD";
            case 2 -> "DEL";
            default -> "UNKNOWN";
        };
    }
}
