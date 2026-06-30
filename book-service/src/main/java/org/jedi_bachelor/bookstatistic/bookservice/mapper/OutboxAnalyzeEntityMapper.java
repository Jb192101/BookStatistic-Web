package org.jedi_bachelor.bookstatistic.bookservice.mapper;

import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.book.OutboxAnalyzeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OutboxAnalyzeEntityMapper {
    @Mapping(source = "readedPages", target = "readedPages")
    List<OutboxAnalyzeDto> toDtoList(List<OutboxAnalyzeEntity> entities);
}
