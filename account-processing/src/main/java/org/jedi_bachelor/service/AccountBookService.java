package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.kafka.dto.BookRatingDto;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.jedi_bachelor.repository.AccountBookRelationShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountBookService {
    @Autowired
    private final AccountBookRelationShipRepository accountBookRelationShipRepository;

    public void updateRelationship(BookRatingDto message) {
        Optional<AccountBookRelationShip> accountBookRelationShipOptional =
                accountBookRelationShipRepository.getAccountBookRelationShipByBookId(message.getBookId());

        if(accountBookRelationShipOptional.isPresent()) {
            accountBookRelationShipOptional.get().setReadedPages(
                    accountBookRelationShipOptional.get().getReadedPages()
                    + message.getNewReadedPages()
            );

            if(Objects.equals(message.getTotalPages(),
                    accountBookRelationShipOptional.get().getReadedPages())) {
                accountBookRelationShipOptional.get().setIsFinished(true);
            }
        }
    }
}
