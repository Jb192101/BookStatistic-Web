package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.AccountBookSpeedReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountBookSpeedReadingRepository extends JpaRepository<AccountBookSpeedReading, Long> {
    Optional<AccountBookSpeedReading> getAccountBookSpeedReadingByAccountId(Long id);
    List<AccountBookSpeedReading> findByAccountId(Long id);
}
