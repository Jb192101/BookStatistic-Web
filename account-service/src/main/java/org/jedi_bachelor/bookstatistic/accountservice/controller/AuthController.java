package org.jedi_bachelor.bookstatistic.accountservice.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.service.UserService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.PasswordInvalidException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserAlreadyExistsInSystemException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) throws UserAlreadyExistsInSystemException, PasswordInvalidException {
        UserDto userDto = this.userService.register(dto);

        return ResponseEntity.ok(userDto);
    }
}
