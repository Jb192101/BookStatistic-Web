package org.jedi_bachelor.bookstatistic.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.request.account.LoginDto;
import org.jedi_bachelor.bookstatistic.dto.request.account.RegisterDto;
import org.jedi_bachelor.bookstatistic.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(LoginDto dto, HttpServletRequest request) {
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(RegisterDto dto, HttpServletRequest request) {
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body(null);
    }
}
