package org.jedi_bachelor.bookstatistic.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.request.notification.BroadcastMessage;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Класс для рассылок сообщений по почте
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/broadcast")
    public ResponseEntity<?> sendBroadcastMessage(BroadcastMessage message) {
        this.emailService.sendBroadcastMessage(message);

        return ResponseEntity.ok(new SuccessResponse(
                HttpStatus.OK.value(), Map.of("sended", true))
        );
    }
}
