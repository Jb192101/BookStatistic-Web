package org.jedi_bachelor.bookstatistic.bookservice.converter;

import org.jedi_bachelor.bookstatistic.bookservice.entity.Book;
import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookCreationDto;
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
        Book book = new Book();
        book.setTitle(dto.title());
        book.setDescription(dto.description());

        return book;
    }
}
