package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.util.UUID;

public record UserBookReportDto(
        UUID userId,
        String genres
) {
}
