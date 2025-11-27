package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.Role;
import org.jedi_bachelor.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthorityService implements UserDetailsService {
    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== LOADING USER BY USERNAME: " + username + " ===");

        Optional<Account> account = accountRepository.findByUsername(username);

        if (account.isEmpty()) {
            System.out.println("User not found: " + username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        Account user = account.get();
        System.out.println("User found: " + user.getUsername());
        System.out.println("User password: " + user.getPassword());
        System.out.println("User role: " + user.getAccountType());
        System.out.println("User authorities: " + user.getAuthorities());

        return user;
    }

    public Boolean saveUser(Account account) {
        Optional<Account> accountFromDB = accountRepository.findByUsername(account.getUsername());

        if(accountFromDB.isPresent()) {
            return false;
        }

        account.setAccountType("ROLE_SIMPLE_USER");
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
        return true;
    }
}
