package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountId(Long id);
    boolean existsByAccountEmail(String email);
    boolean existsByAccountUsername(String username);
    Optional<Account> getAccountById(Long id);
    Optional<Account> getAccountByUsername(String username);
}
