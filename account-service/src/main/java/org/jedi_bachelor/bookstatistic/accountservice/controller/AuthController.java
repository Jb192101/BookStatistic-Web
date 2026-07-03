package org.jedi_bachelor.bookstatistic.accountservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.dto.JwtResponse;
import org.jedi_bachelor.bookstatistic.accountservice.service.UserService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.LoginDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.PasswordInvalidException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserAlreadyExistsInSystemException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    // Аккаунт по этому полностью не setup-ится
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) throws UserAlreadyExistsInSystemException, PasswordInvalidException {
        UserDto userDto = this.userService.register(dto);

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        JwtResponse tokens = this.userService.login(loginDto);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestParam String refreshToken) {
        JwtResponse tokens = this.userService.refreshToken(refreshToken);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        this.userService.logout(refreshToken);

        return ResponseEntity.ok().build();
    }
}
