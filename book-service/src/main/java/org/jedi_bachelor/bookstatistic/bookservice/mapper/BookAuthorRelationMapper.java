package org.jedi_bachelor.bookstatistic.bookservice.mapper;

import org.jedi_bachelor.bookstatistic.bookservice.entity.BookAuthorRelation;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookAuthorRelationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookAuthorRelationMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "author.id", target = "authorId")
    BookAuthorRelationDto toDto(BookAuthorRelation relation);

    List<BookAuthorRelationDto> toDtoList(List<BookAuthorRelation> relations);

    @Mapping(source = "bookId", target = "book.id")
    @Mapping(source = "authorId", target = "author.id")
    BookAuthorRelation toEntity(BookAuthorRelationDto dto);
}
