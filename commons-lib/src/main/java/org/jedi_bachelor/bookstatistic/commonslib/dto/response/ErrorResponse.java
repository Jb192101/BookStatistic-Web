package org.jedi_bachelor.bookstatistic.commonslib.dto.response;

import java.sql.Timestamp;

public record ErrorResponse(
        String title,
        String message,
        int statusCode,
        Timestamp time
) {
}
