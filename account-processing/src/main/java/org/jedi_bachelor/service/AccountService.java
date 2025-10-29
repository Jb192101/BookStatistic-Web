package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.kafka.KafkaProducer;
import org.jedi_bachelor.kafka.dto.BookRatingDto;
import org.jedi_bachelor.kafka.dto.KafkaDtoMessage;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    @Autowired
    private final RatingService ratingService;
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final KafkaProducer kafkaProducer;

    public void updateUserRating(KafkaDtoMessage message) {
        if(message instanceof BookRatingDto) {
            ratingService.calculateNewRating((BookRatingDto) message);
        }
    }

    public Optional<Account> getAccountById(Long id) {
        if(!accountRepository.existsByAccountId(id))
            return Optional.empty();

        return accountRepository.getAccountById(id);
    }

    public void deleteAccountById(Long id) {
        accountRepository.deleteById(id);
    }

    public Boolean updateAccount(Account account) {
        if(!accountRepository.existsByAccountId(account.getId())) {
            return false;
        }

        accountRepository.save(account);

        return true;
    }

    public Boolean addNewAccount(Account account) {
        if(accountRepository.existsByAccountId(account.getId()) ||
                (accountRepository.existsByAccountUsername(account.getUsername())) &&
                        accountRepository.existsByAccountUsername(account.getUsername())) {
            return false;
        }

        accountRepository.save(account);

        return true;
    }

    public Optional<Account> getAccountByLogin(String login) {
        return accountRepository.getAccountByUsername(login);
    }
}
