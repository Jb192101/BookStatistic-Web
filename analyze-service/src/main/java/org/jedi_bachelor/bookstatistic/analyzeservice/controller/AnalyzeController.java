package org.jedi_bachelor.bookstatistic.analyzeservice.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.analyzeservice.service.AnalyzeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/analyze")
@RequiredArgsConstructor
public class AnalyzeController {
    private final AnalyzeService analyzeService;

    public ResponseEntity<?> analyzeUser(UUID userId) {
        return null;
    }

    public ResponseEntity<?> analyzeRandomThousandUsers() {
        return null;
    }
}
