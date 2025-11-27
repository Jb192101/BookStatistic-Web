package org.jedi_bachelor.service.interfaces;

import org.jedi_bachelor.dto.RegistrationRequest;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.FriendRelationship;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    List<FriendRelationship> getFriendsOfAccount(Account account);
    Boolean addNewFriend(Long ourId, Long id);
    Optional<Account> getAccountById(Long id);
    void deleteAccountById(Long id);
    Boolean updateAccount(Account account);
    Boolean addNewAccount(Account account);
    Account registerUser(RegistrationRequest request);
    Optional<Account> getAccountByUsername(String login);
}
