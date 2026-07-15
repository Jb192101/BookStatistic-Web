package org.jedi_bachelor.bookstatistic.commonslib.dto.response;

@Deprecated
public record SuccessResponse(
        int statusCode,
        Object content
) {
}
