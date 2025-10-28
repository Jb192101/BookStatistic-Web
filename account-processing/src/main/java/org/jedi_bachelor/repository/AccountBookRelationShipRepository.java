package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountBookRelationShipRepository extends JpaRepository<AccountBookRelationShip, Long> {
    Optional<AccountBookRelationShip> getAccountBookRelationShipByBookId(Long bookId);
    boolean existsAccountBookRelationShipByBookId(Long bookId);
    boolean existsAccountBookRelationShipByAccount(Account account);
}
