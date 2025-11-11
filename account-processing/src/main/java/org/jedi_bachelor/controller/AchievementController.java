package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.Achievement;
import org.jedi_bachelor.service.AccountService;
import org.jedi_bachelor.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {
    @Autowired
    private final AchievementService achievementService;
    @Autowired
    private final AccountService accountService;

    @GetMapping("/{userId}")
    public String getUserAchievements(Model model, @RequestParam Long userId) {
        List<Achievement> achievements = this.achievementService.getAchievementsOfUser(userId);

        model.addAttribute("achievements-list", achievements);

        Optional<Account> account = accountService.getAccountById(userId);
        String name;
        if(account.isPresent()) {
            name = account.get().getUsername();
        } else {
            name = "НЕИЗВЕСТНЫЙ";
        }

        model.addAttribute("username", name);

        return "user-achievements";
    }
}
