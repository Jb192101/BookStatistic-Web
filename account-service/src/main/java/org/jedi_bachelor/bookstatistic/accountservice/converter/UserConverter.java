package org.jedi_bachelor.bookstatistic.accountservice.converter;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.converter.Converter;
import org.jedi_bachelor.bookstatistic.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

        userProfile.setRole(UserRole.SIMPLE_USER);
        userProfile.setName(dto.username());
        userProfile.setEmail(dto.username());
        userProfile.setHashPassword(this.passwordEncoder.encode(dto.password()));
        userProfile.setEnableEmail(dto.enableEmail());
        userProfile.setTelegramAddress(dto.telegram());

        return null;
    }
}
