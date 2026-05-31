package org.jedi_bachelor.bookstatistic.accountservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.dto.JwtResponse;
import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.jedi_bachelor.bookstatistic.accountservice.service.AuthService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.LoginDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.account.RegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginDto loginDto) {
        JwtResponse tokens = this.authService.login(loginDto);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto registerDto) {
        UserDto newUser = this.authService.register(registerDto);

        return ResponseEntity.status(201).body(newUser);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestParam String refreshToken) {
        JwtResponse tokens = this.authService.refreshToken(refreshToken);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        this.authService.logout(refreshToken);

        return ResponseEntity.ok().build();
    }
}
