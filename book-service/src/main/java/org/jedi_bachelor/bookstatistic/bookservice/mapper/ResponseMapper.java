package org.jedi_bachelor.bookstatistic.bookservice.mapper;

import org.jedi_bachelor.bookstatistic.bookservice.entity.Response;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Stars;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.ResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResponseMapper {
    @Mapping(target = "stars", expression = "java(mapStarsToInt(entity.getStars()))")
    ResponseDto toDto(Response entity);

    List<ResponseDto> toDtoList(List<Response> entities);

    @Mapping(target = "stars", expression = "java(mapIntToStars(dto.stars()))")
    Response toEntity(ResponseDto dto);

    /**
     * Stars enum -> int
     */
    default int mapStarsToInt(Stars stars) {
        if (stars == null) return 0;
        return stars.getValue();
    }

    /**
     * int -> Stars enum
     */
    default Stars mapIntToStars(int starsCount) {
        return Stars.from(starsCount);
    }
}
