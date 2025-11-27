package org.jedi_bachelor.service.interfaces;

import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.jedi_bachelor.model.entities.AccountBookSpeedReading;

import java.util.List;

public interface IAccountBookService {
    List<AccountBookRelationShip> getReadingsByUserId(Long userId);
    List<AccountBookRelationShip> getReadingsByBookId(Long bookId);
    void addNewRelation(AccountBookRelationShip relation);
    List<AccountBookSpeedReading> getSpeedOfReading(Long userId);
}
