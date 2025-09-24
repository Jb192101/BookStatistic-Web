package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountId(Long id);
    boolean existsByAccountEmail(String email);
    boolean existsByAccountUsername(String username);
    Account getAccountById(Long id);
}
