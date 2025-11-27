package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.FriendAccount;
import org.jedi_bachelor.model.entities.FriendRelationship;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    @Autowired
    private final AccountService accountService;

    @GetMapping("/{id}")
    public String getAccountById(Model model, @PathVariable Long id) {
        Optional<Account> account = this.accountService.getAccountById(id);

        if(account.isPresent()) {
            model.addAttribute("account", account.get());

            Set<FriendAccount> friends = new HashSet<>();
            List<FriendRelationship> friendRelationshipList = accountService.getFriendsOfAccount(account.get());

            for(FriendRelationship f : friendRelationshipList) {
                FriendAccount friendAccount = new FriendAccount();
                if(Objects.equals(f.getFriend1().getId(), id)) {
                    friendAccount.setId(f.getFriend2().getId());
                    friendAccount.setUsername(f.getFriend2().getUsername());
                    friendAccount.setRating(f.getFriend2().getRating());
                } else {
                    friendAccount.setId(f.getFriend1().getId());
                    friendAccount.setUsername(f.getFriend2().getUsername());
                    friendAccount.setRating(f.getFriend2().getRating());
                }
                friendAccount.updateUrl();
            }

            model.addAttribute("friends", friends);
        } else {
            //model.addAttribute("error", "Не удалось найти аккаунт!");
        }

        return "account-page";
    }

    @PostMapping("{id}/add-to-friend/{ourId}")
    public ResponseEntity<String> addNewFriend(@PathVariable Long id, @PathVariable Long ourId) {
        Boolean isAdded = accountService.addNewFriend(ourId, id);
        if(isAdded) {
            return ResponseEntity.ok("Дружба успешно установлена!");
        } else {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);

        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Account> addNewAccount(@RequestBody Account account) {
        Boolean result = accountService.addNewAccount(account);
        if(!result) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @PatchMapping
    public ResponseEntity<Account> updateAccount(@RequestBody Account account) {
        Boolean result = accountService.updateAccount(account);

        if(result)
            return ResponseEntity.ok(account);
        else
            return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Account> deleteAccount(@RequestParam Long id) {
        accountService.deleteAccountById(id);

        return ResponseEntity.status(200).build();
    }
}
