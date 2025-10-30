package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.jedi_bachelor.model.entities.AccountBookSpeedReading;
import org.jedi_bachelor.repository.AccountBookRelationShipRepository;
import org.jedi_bachelor.repository.AccountBookSpeedReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountBookService {
    @Autowired
    private final AccountBookRelationShipRepository accountBookRelationShipRepository;
    @Autowired
    private final AccountBookSpeedReadingRepository accountBookSpeedReadingRepository;

    public List<AccountBookRelationShip> getReadingsByUserId(Long userId) {
        List<AccountBookRelationShip> relationShips =
                this.accountBookRelationShipRepository.findAll();

        List<AccountBookRelationShip> neededRelations = new ArrayList<>();
        for(AccountBookRelationShip relation : relationShips) {
            if(Objects.equals(relation.getAccount().getId(), userId)) {
                neededRelations.add(relation);
            }
        }

        return neededRelations;
    }

    public List<AccountBookRelationShip> getReadingsByBookId(Long bookId) {
        List<AccountBookRelationShip> relationShips =
                this.accountBookRelationShipRepository.findAll();

        List<AccountBookRelationShip> neededRelations = new ArrayList<>();
        for(AccountBookRelationShip relation : relationShips) {
            if(Objects.equals(relation.getBook().getId(), bookId)) {
                neededRelations.add(relation);
            }
        }

        return neededRelations;
    }

    public void addNewRelation(AccountBookRelationShip relation) {
        this.accountBookRelationShipRepository.save(relation);

        //Long id = relation.getAccount().getId();
        //Optional<AccountBookSpeedReading> speed = this.accountBookSpeedReadingRepository.getAccountBookSpeedReadingByAccountId(id);
        //if(speed.isPresent()) {
        //    speed.get().setSpeedReadingPages(speed.get().getSpeedReadingPages());
        //}
    }

    public List<AccountBookSpeedReading> getSpeedOfReading(Long userId) {
        List<AccountBookSpeedReading> speeds = this.accountBookSpeedReadingRepository.findAll();

        List<AccountBookSpeedReading> neededSpeeds = new ArrayList<>();
        for(AccountBookSpeedReading speed : speeds) {
            if(Objects.equals(speed.getAccount().getId(), userId))
                neededSpeeds.add(speed);
        }

        return neededSpeeds;
    }
}
