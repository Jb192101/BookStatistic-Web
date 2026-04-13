package org.jedi_bachelor.bookstatistic.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.request.account.UserCreationDto;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(new SuccessResponse());
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(new SuccessResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(new SuccessResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id) {
        return ResponseEntity.ok().body(new SuccessResponse());
    }

    @PostMapping
    public ResponseEntity<?> addNewUser(@RequestBody UserCreationDto dto) {
        return ResponseEntity.ok().body(new SuccessResponse());
    }
}
