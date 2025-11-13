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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final AccountService accountService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Вход в книжную статистику");
        model.addAttribute("currentYear", LocalDate.now().getYear());

        // Случайная мотивирующая цитата о чтении
        String[] quotes = {
                "Чтение — это беседа с мудрейшими людьми прошлых веков",
                "Книги — корабли мысли, странствующие по волнам времени",
                "Статистика чтения — это карта твоих литературных путешествий"
        };
        String randomQuote = quotes[new Random().nextInt(quotes.length)];
        model.addAttribute("motivationalQuote", randomQuote);

        return "account/login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@ModelAttribute LoginRequest loginRequest) {
        Optional<Account> userOpt = accountService.getAccountByLogin(loginRequest.getLogin());

        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Неверные учетные данные");
        }

        Account account = userOpt.get();

        String token = jwtUtil.generateToken(account.getUsername(), account.getAccountType().name());

        return ResponseEntity.ok(new LoginResponse(token, account.getAccountType().name()));
    }
}
