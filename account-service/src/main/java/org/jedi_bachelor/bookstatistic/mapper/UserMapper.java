package org.jedi_bachelor.bookstatistic.mapper;

import org.jedi_bachelor.bookstatistic.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
