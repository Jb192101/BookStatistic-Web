package org.jedi_bachelor.bookstatistic.dto.response;

import java.sql.Timestamp;

public record ErrorResponse(
        String title,
        String message,
        int statusCode,
        Timestamp time
) {
}
