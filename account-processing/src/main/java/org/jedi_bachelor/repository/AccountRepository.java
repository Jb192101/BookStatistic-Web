package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsById(Long id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Account> getAccountById(Long id);
    Optional<Account> getAccountByUsername(String username);
}
