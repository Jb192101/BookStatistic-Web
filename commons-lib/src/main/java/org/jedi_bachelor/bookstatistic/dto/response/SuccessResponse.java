package org.jedi_bachelor.bookstatistic.dto.response;

public record SuccessResponse(
        int statusCode,
        Object content
) {
}
