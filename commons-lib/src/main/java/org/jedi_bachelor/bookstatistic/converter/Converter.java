package org.jedi_bachelor.bookstatistic.converter;

/**
 * Интерфейс конвертации DTO для создания в entity
 *
 * @param <E> тип сущности
 * @param <D> тип DTO
 */
public interface Converter<E, D> {
    E convert(D dto);
}
