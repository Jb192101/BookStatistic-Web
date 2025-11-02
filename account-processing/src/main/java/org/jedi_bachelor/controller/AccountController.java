package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.email.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final EmailService emailService;

    @GetMapping("/{id}")
    public String getAccountById(Model model, @PathVariable Long id) {
        Optional<Account> account = this.accountService.getAccountById(id);
        if(account.isPresent()) {
            model.addAttribute("account", account.get());
        } else {
            //model.addAttribute("error", "Не удалось найти аккаунт!");
        }

        return "account-page";
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);

        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Account> addNewAccount(@RequestBody Account account) {
        Boolean result = accountService.addNewAccount(account);
        if(!result) {
            emailService.sendEmail(account.getEmail(), "Ошибка регистрации аккаунта!", "some text");

            return ResponseEntity.notFound().build();
        }
        // Работа с emailService
        emailService.sendEmail(account.getEmail(), "Аккаунт зарегистрирован!", "some text");

        return ResponseEntity.ok(account);
    }

    @PatchMapping
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {
        Boolean result = accountService.updateAccount(account);

        if(result)
            return ResponseEntity.ok(account);
        else
            return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Account> deleteAccount(@RequestParam Long id) {
        accountService.deleteAccountById(id);

        return ResponseEntity.status(200).build();
    }
}
