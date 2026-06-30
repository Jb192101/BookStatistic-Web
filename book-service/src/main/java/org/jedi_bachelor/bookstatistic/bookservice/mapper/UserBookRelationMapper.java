package org.jedi_bachelor.bookstatistic.bookservice.mapper;

import org.jedi_bachelor.bookstatistic.bookservice.entity.UserBookRelation;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserBookRelationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserBookRelationMapper {
    @Mapping(source = "readedPages", target = "readedPages")
    UserBookRelationDto toDto(UserBookRelation book);

    @Mapping(source = "readedPages", target = "readedPages")
    List<UserBookRelationDto> toDtoList(List<UserBookRelation> books);

    @Mapping(source = "readedPages", target = "readedPages")
    UserBookRelation toEntity(UserBookRelationDto bookDto);
}
