package org.jedi_bachelor.bookstatistic.accountservice.converter;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.accountservice.language.Language;
import org.jedi_bachelor.bookstatistic.commonslib.converter.Converter;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationConverter implements Converter<UserProfile, RegisterDto> {
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfile convert(RegisterDto dto) {
        UserProfile userProfile = new UserProfile();

        userProfile.setBirthDay(dto.birthDay());
        userProfile.setLanguage(Language.valueOf(dto.language()));
        userProfile.setFirstName(dto.firstName());
        userProfile.setMiddleName(dto.middleName());
        userProfile.setLastName(dto.lastName());
        userProfile.setUsername(dto.username());
        userProfile.setPassword(this.passwordEncoder.encode(dto.password()));

        return userProfile;
    }
}
