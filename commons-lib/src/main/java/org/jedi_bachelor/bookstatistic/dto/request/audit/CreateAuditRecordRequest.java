package org.jedi_bachelor.bookstatistic.dto.request.audit;

import java.sql.Timestamp;
import java.util.UUID;

public record CreateAuditRecordRequest(
    UUID id,
    Timestamp time,
    String place,
    String message,
    String whoChange // Имя изменившего данные
) {
}
