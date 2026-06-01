package org.jedi_bachelor.bookstatistic.accountservice.converter;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserConverter implements Converter<UserProfile, RegisterDto> {
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод конвертации DTO в сущность
     *
     * @param dto DTO для конвертации
     * @return сущность
     */
    @Override
    public UserProfile convert(RegisterDto dto) {
        UserProfile userProfile = new UserProfile();

        userProfile.setId(UUID.randomUUID());
        userProfile.setName(dto.username());
        userProfile.setHashPassword(this.passwordEncoder.encode(dto.password()));
        userProfile.setBirthDay(dto.birthDay());
        userProfile.setLanguage(dto.language());

        return userProfile;
    }
}
