package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.Card;
import org.jedi_bachelor.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public Integer getBalance(Long id) {
        Optional<Card> card = cardRepository.getCardById(id);

        if(card.isPresent()) {
            return card.get().getBalance();
        }

        return 0;
    }
}
