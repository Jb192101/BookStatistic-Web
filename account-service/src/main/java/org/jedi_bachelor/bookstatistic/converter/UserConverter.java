package org.jedi_bachelor.bookstatistic.converter;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.entity.User;
import org.jedi_bachelor.bookstatistic.entity.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter implements Converter<User, RegisterDto> {
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод конвертации DTO в сущность
     *
     * @param dto DTO для конвертации
     * @return сущность
     */
    @Override
    public User convert(RegisterDto dto) {
        User user = new User();

        user.setRole(UserRole.SIMPLE_USER);
        user.setName(dto.username());
        user.setEmail(dto.username());
        user.setHashPassword(this.passwordEncoder.encode(dto.password()));
        user.setEnableEmail(dto.enableEmail());
        user.setTelegramAddress(dto.telegram());

        return null;
    }
}
