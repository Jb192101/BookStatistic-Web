package org.jedi_bachelor.bookstatistic.bookservice.mapper;

import org.jedi_bachelor.bookstatistic.bookservice.entity.BookAuthorRelation;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookAuthorRelationDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookAuthorRelationMapper {
    BookAuthorRelationDto toDto(BookAuthorRelation relation);

    List<BookAuthorRelationDto> toDtoList(List<BookAuthorRelation> relations);

    BookAuthorRelation toEntity(BookAuthorRelationDto dto);
}
