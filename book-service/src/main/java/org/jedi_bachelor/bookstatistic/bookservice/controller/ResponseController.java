package org.jedi_bachelor.bookstatistic.bookservice.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.service.ResponseService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.ResponseDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.ResponseCreateUpdateDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.ResponsePartialKey;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.BookNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.ResponseAlreadyExistsException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.ResponseNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/responses")
public class ResponseController {
    private final ResponseService responseService;

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @PostMapping
    public ResponseEntity<?> addNewResponse(@RequestBody ResponseCreateUpdateDto dto) throws BookNotFoundException, ResponseAlreadyExistsException {
        ResponseDto responseDto = this.responseService.addNewResponse(dto);

        return ResponseEntity.status(201).body(responseDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/books/{bookId}")
    public ResponseEntity<?> getResponse(@PathVariable UUID bookId, @RequestBody ResponsePartialKey key) throws ResponseNotFoundException {
        ResponseDto responseDto = this.responseService.getResponse(bookId, key.userId());

        return ResponseEntity.ok(responseDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR
    @GetMapping
    public ResponseEntity<?> getAllResponses() {
        List<ResponseDto> dtos = this.responseService.getAllResponses();

        return ResponseEntity.ok(dtos);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/books/{bookId}/all")
    public ResponseEntity<?> getAllResponsesOnBook(@PathVariable UUID bookId) {
        List<ResponseDto> dtos = this.responseService.getAllResponsesOnBook(bookId);

        return ResponseEntity.ok(dtos);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getAllResponsesOfUser(@PathVariable UUID userId) {
        List<ResponseDto> dtos = this.responseService.getAllResponsesOfUser(userId);

        return ResponseEntity.ok(dtos);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @PutMapping
    public ResponseEntity<?> updateResponse(@RequestBody ResponseCreateUpdateDto dto) throws ResponseNotFoundException {
        ResponseDto responseDto = this.responseService.updateResponse(dto);

        return ResponseEntity.ok(responseDto);
    }

    // ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteResponse(@PathVariable UUID bookId, @RequestBody ResponsePartialKey key) throws ResponseNotFoundException {
        ResponseDto deleted = this.responseService.deleteResponse(bookId, key.userId());

        return ResponseEntity.ok(deleted);
    }
}
