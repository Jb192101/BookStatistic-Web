package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.aop.aspects.SendEmailMessage;
import org.jedi_bachelor.email.EmailService;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.repository.AccountRepository;
import org.jedi_bachelor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final EmailService emailService;

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(Long id) {
        if(accountRepository.existsByAccountId(id))
            return ResponseEntity.notFound().build();

        Account account = accountRepository.getAccountById(id);

        return ResponseEntity.ok(account);
    }

    @PostMapping
    public ResponseEntity<Account> addNewAccount(Account account) {
        if(accountRepository.existsByAccountId(account.getId()) ||
                (accountRepository.existsByAccountUsername(account.getUsername())) &&
                        accountRepository.existsByAccountUsername(account.getUsername())) {
            emailService.sendEmail(account.getEmail(), "Ошибка регистрации аккаунта!", "some text");

            return ResponseEntity.notFound().build();
        }

        accountRepository.save(account);

        // Работа с emailService
        emailService.sendEmail(account.getEmail(), "Аккаунт зарегистрирован!", "some text");

        return ResponseEntity.ok(account);
    }

    @PatchMapping
    public ResponseEntity<Account> updateAccount(Account account) {
        if(!accountRepository.existsByAccountId(account.getId())) {
            return ResponseEntity.notFound().build();
        }

        accountRepository.save(account);

        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Account> deleteAccount(Long id) {
        
        return null;
    }
}
