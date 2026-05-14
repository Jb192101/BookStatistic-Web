package org.jedi_bachelor.bookstatistic.analyzeservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/analyze")
public class AnalyzeController {
    public ResponseEntity<?> analyzeUser(UUID userId) {
        return null;
    }

    public ResponseEntity<?> analyzeRandomThousandUsers() {
        return null;
    }
}
