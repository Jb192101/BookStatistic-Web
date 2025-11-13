package org.jedi_bachelor.math;
/*
Функции:
    x(a, c, oldRating), где a - кол-во новых достижений, c - количество прочитанных страниц за неделю,
    oldRating - старый рейтинг
    newRating(x), где x - значение аргумента, который определяется по кол-ву прочитанных страниц и достижений

    Значение второй ф-ии должно определять новое значение рейтинга пользователя
 */

public interface MathDistribution {
    Float x(int a, int c, int oldRating);
    Integer newRating(float x);
}
