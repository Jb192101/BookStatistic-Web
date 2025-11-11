package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.FriendRelationship;
import org.jedi_bachelor.repository.AccountRepository;
import org.jedi_bachelor.repository.FriendRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    @Autowired
    private final RatingService ratingService;
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final FriendRelationshipRepository friendRelationshipRepository;

    /*
    public void updateUserRating(KafkaDtoMessage message) {
        if(message instanceof BookRatingDto) {
            ratingService.calculateNewRating((BookRatingDto) message);
        }
    }
     */

    /*
    Метод для выдачи списка друзей аккаунта по аккаунту
     */
    public List<FriendRelationship> getFriendsOfAccount(Account account) {
        List<FriendRelationship> result1 = this.friendRelationshipRepository.findFriendRelationshipByFriend1(account);
        List<FriendRelationship> result2 = this.friendRelationshipRepository.findFriendRelationshipByFriend2(account);

        List<FriendRelationship> result = new ArrayList<>();
        result.addAll(result1);
        result.addAll(result2);

        return result;
    }

    /*
    Метод для добавления нового друга
     */
    public Boolean addNewFriend(Long ourId, Long id) {
        Optional<Account> account1 = this.accountRepository.getAccountById(ourId);
        Optional<Account> account2 = this.accountRepository.getAccountById(id);

        if(account1.isPresent() && account2.isPresent()) {
            if(!this.friendRelationshipRepository.existsByFriend1AndFriend2(account1.get(), account2.get()) &&
            !this.friendRelationshipRepository.existsByFriend1AndFriend2(account2.get(), account1.get())) {
                FriendRelationship friendRelationship = new FriendRelationship();
                friendRelationship.setFriend1(account1.get());
                friendRelationship.setFriend2(account2.get());
                this.friendRelationshipRepository.save(friendRelationship);
                return true;
            }
        }

        return false;
    }

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
