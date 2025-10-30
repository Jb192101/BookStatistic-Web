package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface AccountBookRelationShipRepository extends JpaRepository<AccountBookRelationShip, Long> {
    Optional<AccountBookRelationShip> getAccountBookRelationShipByBookId(Long bookId);
    boolean existsAccountBookRelationShipByBookId(Long bookId);
    boolean existsAccountBookRelationShipByAccount(Account account);
}
