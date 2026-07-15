package org.jedi_bachelor.bookstatistic.accountservice.mapper;

import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserProfile profile);

    List<UserDto> toDtoList(List<UserProfile> profiles);

    UserProfile toEntity(UserDto dto);
}
