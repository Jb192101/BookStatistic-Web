package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.jedi_bachelor.model.entities.AccountBookSpeedReading;
import org.jedi_bachelor.service.AccountBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reading")
@RequiredArgsConstructor
public class AccountBookController {
    @Autowired
    private final AccountBookService accountBookService;

    @GetMapping("/books/{userId}")
    public ResponseEntity<List<AccountBookRelationShip>> getAllReadings(@PathVariable Long userId) {
        try {
            List<AccountBookRelationShip> relationShips = this.accountBookService.getReadingsByUserId(userId);

            return ResponseEntity.ok(relationShips);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{bookId}/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<AccountBookRelationShip>> getAllUsers(@PathVariable Long bookId) {
        try {
            List<AccountBookRelationShip> relationShips = this.accountBookService.getReadingsByBookId(bookId);

            return ResponseEntity.ok(relationShips);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/statistic/{userId}")
    public ResponseEntity<List<AccountBookSpeedReading>> getSpeedOfReading(@PathVariable Long userId) {
        List<AccountBookSpeedReading> speeds = this.accountBookService.getSpeedOfReading(userId);

        return ResponseEntity.ok(speeds);
    }
}
