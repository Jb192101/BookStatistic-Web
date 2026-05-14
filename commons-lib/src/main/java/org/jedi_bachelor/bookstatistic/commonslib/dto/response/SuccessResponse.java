package org.jedi_bachelor.bookstatistic.commonslib.dto.response;

public record SuccessResponse(
        int statusCode,
        Object content
) {
}
