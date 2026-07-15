package org.jedi_bachelor.bookstatistic.commonslib.dto.request.analyze;

import java.util.List;
import java.util.UUID;

public record UserBookReportCreateDto(
            UUID userId,
            List<UUID> userBookIds
) {
}
