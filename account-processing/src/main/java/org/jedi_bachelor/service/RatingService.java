package org.jedi_bachelor.service;

import org.jedi_bachelor.kafka.dto.BookRatingDto;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
    @Autowired
    private AccountRepository accountRepository;

    public void calculateNewRating(Long userId, BookRatingDto bookDto) {
        Account account = accountRepository.getAccountById(userId);

        Integer addedValueRating = bookDto.getNewReadedPages() * 2;

        account.setRating(account.getRating() + addedValueRating);

        accountRepository.save(account);
    }
}
