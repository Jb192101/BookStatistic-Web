package org.jedi_bachelor.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.dto.RegistrationRequest;
import org.jedi_bachelor.email.EmailService;
import org.jedi_bachelor.model.entities.*;
import org.jedi_bachelor.model.enums.AccountType;
import org.jedi_bachelor.repository.AccountBookRelationShipRepository;
import org.jedi_bachelor.repository.AccountBookSpeedReadingRepository;
import org.jedi_bachelor.repository.AccountRepository;
import org.jedi_bachelor.repository.FriendRelationshipRepository;
import org.jedi_bachelor.service.interfaces.IAccountService;
import org.jedi_bachelor.service.interfaces.IMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService, IMainService {
    @Autowired
    private final AccountBookService accountBookService;
    @Autowired
    private final RatingService ratingService;
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final FriendRelationshipRepository friendRelationshipRepository;
    @Autowired
    private final AccountBookRelationShipRepository accountBookRelationShipRepository;
    @Autowired
    private final AccountBookSpeedReadingRepository accountBookSpeedReadingRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final EmailService emailService;

    /*
    public void updateUserRating(KafkaDtoMessage message) {
        if(message instanceof BookRatingDto) {
            ratingService.calculateNewRating((BookRatingDto) message);
        }
    }
     */

    /*
    * Метод для выдачи списка друзей аккаунта по аккаунту
    * @param account (Account) - аккаунт, у которого надо найти список FriendRelationShip
    * @return result (List<FriendRelationShip) - список отношений аккаунта
     */
    @Override
    public List<FriendRelationship> getFriendsOfAccount(Account account) {
        List<FriendRelationship> result1 = this.friendRelationshipRepository.findFriendRelationshipByFriend1(account);
        List<FriendRelationship> result2 = this.friendRelationshipRepository.findFriendRelationshipByFriend2(account);

        List<FriendRelationship> result = new ArrayList<>();
        result.addAll(result1);
        result.addAll(result2);

        return result;
    }

    /*
    * Метод для добавления нового друга
    * @param ourId (Long) - наш собственный id
    * @param id (Long) - id аккаунт, который надо добавить в друзья
     */
    @Override
    public Boolean addNewFriend(Long ourId, Long id) {
        Optional<Account> account1 = this.accountRepository.findById(ourId);
        Optional<Account> account2 = this.accountRepository.findById(id);

        if(account1.isPresent() && account2.isPresent()) {
            if(!this.friendRelationshipRepository.existsByFriend1AndFriend2(account1.get(), account2.get()) &&
            !this.friendRelationshipRepository.existsByFriend1AndFriend2(account2.get(), account1.get())) {
                FriendRelationship friendRelationship1= new FriendRelationship();
                friendRelationship1.setFriend1(account1.get());
                friendRelationship1.setFriend2(account2.get());
                this.friendRelationshipRepository.save(friendRelationship1);

                FriendRelationship friendRelationship2 = new FriendRelationship();
                friendRelationship2.setFriend1(account2.get());
                friendRelationship2.setFriend2(account1.get());
                this.friendRelationshipRepository.save(friendRelationship2);
                return true;
            }
        }

        return false;
    }

    /*
    * Метод для выдачи аккаунта по ID
    * @param id - id аккаунта
    * @return аккаунт с таким ID, если есть, и Optional.empty() если нет
     */
    @Override
    public Optional<Account> getAccountById(Long id) {
        if(!accountRepository.existsById(id))
            return Optional.empty();

        return accountRepository.findById(id);
    }

    /*
    * Метод удаления аккаунта по ID
    * @param id (Long) - id аккаунт, который надо удалить
     */
    @Transactional
    @Override
    public void deleteAccountById(Long id) {
        if(this.accountRepository.existsById(id)) {
            accountRepository.deleteById(id);

            // Удаление из друзей
            List<FriendRelationship> friendRelationshipList1 = this.friendRelationshipRepository.findFriendRelationshipByFriend1(
                    this.accountRepository.findById(id).get()
            );
            List<FriendRelationship> friendRelationshipList2 = this.friendRelationshipRepository.findFriendRelationshipByFriend2(
                    this.accountRepository.findById(id).get()
            );
            List<FriendRelationship> allFriends = new ArrayList<>();
            allFriends.addAll(friendRelationshipList1);
            allFriends.addAll(friendRelationshipList2);

            for(FriendRelationship friend : allFriends) {
                friendRelationshipRepository.delete(friend);
            }

            // Удаление отношений аккаунт-чтение книг
            List<AccountBookRelationShip> accountBookRelationShips1 = this.accountBookRelationShipRepository.findByAccountId(
                    id
            );
            List<AccountBookRelationShip> accountBookRelationShips2 = this.accountBookRelationShipRepository.findByAccountId(
                    id
            );
            List<AccountBookRelationShip> accountBookRelationShips = new ArrayList<>();
            accountBookRelationShips.addAll(accountBookRelationShips1);
            accountBookRelationShips.addAll(accountBookRelationShips2);

            for(AccountBookRelationShip relation : accountBookRelationShips) {
                accountBookRelationShipRepository.delete(relation);
            }

            // Удаление отношений аккаунт-скорость чтения
            List<AccountBookSpeedReading> accountBookSpeeds1 = this.accountBookSpeedReadingRepository.findByAccountId(
                    id
            );
            List<AccountBookSpeedReading> accountBookSpeeds2 = this.accountBookSpeedReadingRepository.findByAccountId(
                    id
            );
            List<AccountBookSpeedReading> accountBookSpeeds = new ArrayList<>();
            accountBookSpeeds.addAll(accountBookSpeeds1);
            accountBookSpeeds.addAll(accountBookSpeeds2);

            for(AccountBookSpeedReading s : accountBookSpeeds) {
                accountBookSpeedReadingRepository.delete(s);
            }
        }
    }

    /*
    * Метод для изменения аккаунта
    * @param account (Account) - аккаунт с изменёнными данными, который надо сохранить
    * @return true, если аккаунт обновлён, false если нет.
     */
    @Override
    public Boolean updateAccount(Account account) {
        if(!accountRepository.existsById(account.getId())) {
            return false;
        }

        accountRepository.save(account);

        return true;
    }

    /*
    * Метод для добавления нового аккаунта (прерогатива админа)
    * @param account (Account) - аккаунт, который надо добавить
    * @return true, если аккаунт добавлен, false если нет.
     */
    @Override
    public Boolean addNewAccount(Account account) {
        if(accountRepository.existsById(account.getId()) ||
                accountRepository.existsByUsername(account.getUsername()) ||
                accountRepository.existsByEmail(account.getEmail())) {
            emailService.sendEmail(account.getEmail(), "Ошибка регистрации аккаунта!", "some text");
            return false;
        }
        emailService.sendEmail(account.getEmail(), "Аккаунт зарегистрирован!", "some text");
        accountRepository.save(account);
        return true;
    }

    /*
    * Метод регистрации нового пользователя (для пользователей)
    * @param request (RegistrationRequest) - DTO с необходимой информацией для пользователя
    * @return account (Account) - новый зарегистрированный аккаунт
     */
    @Override
    public Account registerUser(RegistrationRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Account user = new Account();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPasswordConfirm()));
        user.setUsername(request.getUsername());
        user.setAccountType("ROLE_SIMPLE_USER");

        String textToEmail =
                "Привет, " + user.getUsername() + "!\n" + "Поздравляю с регистрацией на сайте BookStatistic-Web.\n\n" +
                "Радуйся этому, тварь.";

        emailService.sendEmail(user.getEmail(), "Аккаунт зарегистрирован!", textToEmail);

        return accountRepository.save(user);
    }

    /*
    * Метод для получения аккаунта по логину
    * @param login (String) - логин пользователя
    * @return аккаунт с таким логиком
     */
    @Override
    public Optional<Account> getAccountByUsername(String login) {
        return this.accountRepository.findByUsername(login);
    }

    /**
     * Метод для вычисления прогресса по чтению
     * @param user
     * @return
     */
    @Override
    public Map<String, Object> calculateReadingProgress(Account user) {
        Map<String, Object> progress = new HashMap<>();
        int totalBooks = user.getBookRelationships() != null ? user.getBookRelationships().size() : 0;
        int completedBooks = 0;
        int totalPages = 0;

        if (user.getBookRelationships() != null) {
            for (AccountBookRelationShip relation : user.getBookRelationships()) {
                if (relation.getIsFinished()) {
                    completedBooks++;
                }
                totalPages += relation.getReadedPages();
            }
        }

        progress.put("totalBooks", totalBooks);
        progress.put("completedBooks", completedBooks);
        progress.put("totalPages", totalPages);
        progress.put("completionRate", totalBooks > 0 ? (completedBooks * 100 / totalBooks) : 0);

        return progress;
    }

    /**
     * Метод для получения предпочтительного жанра пользователя
     * @param user
     * @return
     */
    @Override
    public String getFavoriteGenre(Account user) {
        if (user.getBookRelationships() == null || user.getBookRelationships().isEmpty()) {
            return "Еще не определен";
        }

        Map<String, Integer> genreCount = new HashMap<>();
        for (AccountBookRelationShip relation : user.getBookRelationships()) {
            String genre = relation.getBook().getGenreType().name();
            genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
        }

        return genreCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Еще не определен");
    }
}
