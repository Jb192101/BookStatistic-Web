package org.jedi_bachelor.model.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
abstract public class Achievement {
    private final Long id;
    private final String title;
    private final String description;

    public abstract Boolean isAchieved(Long userId);
}
