package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.kafka.KafkaConsumer;
import org.jedi_bachelor.kafka.KafkaProducer;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.jedi_bachelor.repository.AccountBookRelationShipRepository;
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

    public List<AccountBookRelationShip> getReadingsByUserId(Long userId) throws InterruptedException {
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
}
