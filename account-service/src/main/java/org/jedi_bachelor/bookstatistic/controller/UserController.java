package org.jedi_bachelor.bookstatistic.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.dto.request.account.UserCreationDto;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.exceptions.UserNotFoundException;
import org.jedi_bachelor.bookstatistic.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserById(@PathVariable UUID id) throws UserNotFoundException {
        UserDto user = this.userService.findUserById(id);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.FOUND.value(), user));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<UserDto> dtos = this.userService.findAll();

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.FOUND.value(), dtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable UUID id) throws UserNotFoundException {
        UserDto dto = this.userService.deleteUser(id);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserDto dto) throws UserNotFoundException {
        UserDto user = this.userService.updateUser(dto);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.OK.value(), user));
    }

    @PostMapping
    public ResponseEntity<?> addNewUser(@RequestBody UserCreationDto dto) {
        UserDto user = this.userService.addNewUser(dto);

        return ResponseEntity.ok().body(new SuccessResponse(HttpStatus.CREATED.value(), user));
    }
}
