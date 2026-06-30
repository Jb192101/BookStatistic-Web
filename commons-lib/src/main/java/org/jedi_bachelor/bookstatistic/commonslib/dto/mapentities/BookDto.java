package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.util.UUID;

public record BookDto(
    UUID id,
    String title,
    String description,
    Integer pages
) {
}
