package org.jedi_bachelor.bookstatistic.accountservice.mapper;

import org.jedi_bachelor.bookstatistic.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserProfile userProfile);

    List<UserDto> toDtoList(List<UserProfile> userProfiles);
}
