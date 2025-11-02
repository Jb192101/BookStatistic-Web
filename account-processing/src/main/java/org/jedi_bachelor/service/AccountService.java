package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
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

    /*
    public void updateUserRating(KafkaDtoMessage message) {
        if(message instanceof BookRatingDto) {
            ratingService.calculateNewRating((BookRatingDto) message);
        }
    }
     */

    public Optional<Account> getAccountById(Long id) {
        if(!accountRepository.existsById(id))
            return Optional.empty();

        return accountRepository.getAccountById(id);
    }

    public void deleteAccountById(Long id) {
        accountRepository.deleteById(id);
    }

    public Boolean updateAccount(Account account) {
        if(!accountRepository.existsById(account.getId())) {
            return false;
        }

        accountRepository.save(account);

        return true;
    }

    public Boolean addNewAccount(Account account) {
        if(accountRepository.existsById(account.getId()) ||
                accountRepository.existsByUsername(account.getUsername()) ||
                accountRepository.existsByEmail(account.getEmail())) {
            return false;
        }
        accountRepository.save(account);
        return true;
    }

    public Optional<Account> getAccountByLogin(String login) {
        return accountRepository.getAccountByUsername(login);
    }
}
