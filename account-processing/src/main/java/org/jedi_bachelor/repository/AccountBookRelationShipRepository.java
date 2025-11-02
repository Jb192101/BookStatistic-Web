package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountBookRelationShipRepository extends JpaRepository<AccountBookRelationShip, Long> {
    Optional<AccountBookRelationShip> getAccountBookRelationShipByBookId(Long bookId);
    boolean existsAccountBookRelationShipByBookId(Long bookId);
    boolean existsAccountBookRelationShipByAccount(Account account);
    List<AccountBookRelationShip> findByAccountId(Long userId);
    List<AccountBookRelationShip> findByBookId(Long bookId);
}
