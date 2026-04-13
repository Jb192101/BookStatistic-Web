package org.jedi_bachelor.bookstatistic.converter;

import org.jedi_bachelor.bookstatistic.dto.request.account.UserCreationDto;
import org.jedi_bachelor.bookstatistic.entity.User;

public class UserConverter implements Converter<User, UserCreationDto> {
    /**
     * Метод конвертации DTO в сущность
     * @param dto DTO для конвертации
     * @return сущность
     */
    @Override
    public User convert(UserCreationDto dto) {
        return null;
    }
}
