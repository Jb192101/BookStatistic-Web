package org.jedi_bachelor.bookstatistic.converter;

import org.jedi_bachelor.bookstatistic.dto.request.book.BookCreationDto;
import org.jedi_bachelor.bookstatistic.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookConverter implements Converter<Book, BookCreationDto> {
    /**
     * Метод конвертации DTO на создание сущности в сущность
     *
     * @param dto DTO создания сущности
     * @return сущность
     */
    @Override
    public Book convert(BookCreationDto dto) {
        return null;
    }
}
