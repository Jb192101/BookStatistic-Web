package org.jedi_bachelor.bookstatistic.bookservice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Stars {
    FIVE_STARS(5),
    FOUR_STARS(4),
    THREE_STARS(3),
    TWO_STARS(2),
    ONE_STAR(1);

    private final int value;

    public static Stars from(int countOfStars) {
        return switch (countOfStars) {
            case 1 -> ONE_STAR;
            case 2 -> TWO_STARS;
            case 3 -> THREE_STARS;
            case 4 -> FOUR_STARS;
            case 5 -> FIVE_STARS;
            default -> ONE_STAR;
        };
    }
}
