package org.jedi_bachelor.bookstatistic.bookservice.mapper;

import org.jedi_bachelor.bookstatistic.bookservice.entity.Book;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(source = "pages", target = "pages")
    BookDto toDto(Book book);

    @Mapping(source = "pages", target = "pages")
    List<BookDto> toDtoList(List<Book> books);

    @Mapping(source = "pages", target = "pages")
    Book toEntity(BookDto bookDto);
}
