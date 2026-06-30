package org.jedi_bachelor.bookstatistic.analyzeservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.service.ModelTrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/analyze/training")
@RequiredArgsConstructor
@Slf4j
public class ModelTrainingController {
    private final ModelTrainerService modelTrainerService;

    @PostMapping("/run")
    public ResponseEntity<String> runTraining() {
        log.info("Training requested via API");

        try {
            this.modelTrainerService.train();

            return ResponseEntity.ok("Training completed successfully");
        } catch (Exception e) {
            log.error("Training failed", e);
            return ResponseEntity.internalServerError()
                    .body("Training failed: " + e.getMessage());
        }
    }
}

