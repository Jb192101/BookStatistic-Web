package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.security.JwtUtil;
import org.jedi_bachelor.security.LoginRequest;
import org.jedi_bachelor.security.LoginResponse;
import org.jedi_bachelor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Account> userOpt = accountService.getAccountByLogin(loginRequest.getLogin());

        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Неверные учетные данные");
        }

        Account account = userOpt.get();

        String token = jwtUtil.generateToken(account.getUsername(), account.getAccountType().name());

        return ResponseEntity.ok(new LoginResponse(token, account.getAccountType().name()));
    }
}
