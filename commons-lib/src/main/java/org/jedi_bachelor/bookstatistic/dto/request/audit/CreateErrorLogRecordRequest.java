package org.jedi_bachelor.bookstatistic.dto.request.audit;

import java.sql.Timestamp;
import java.util.UUID;

public record CreateErrorLogRecordRequest(
        UUID id,
        Timestamp time,
        String errorPlace,
        String errorMessage
) {
}
