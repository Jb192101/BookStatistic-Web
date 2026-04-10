package org.jedi_bachelor.bookstatistic.mapper;

import org.jedi_bachelor.bookstatistic.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.entity.Book;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    List<BookDto> toDtoList(List<Book> books);

    Book toEntity(BookDto bookDto);
}
