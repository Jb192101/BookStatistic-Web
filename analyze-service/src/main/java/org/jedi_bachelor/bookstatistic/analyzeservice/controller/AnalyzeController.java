package org.jedi_bachelor.bookstatistic.analyzeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.analyzeservice.service.AnalyzeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/analyze")
@RequiredArgsConstructor
@Tag(name = "Контроллер сервиса анализа", description = "Для анализа литературных предпочтений пользователей")
public class AnalyzeController {
    private final AnalyzeService analyzeService;

    @RolesAllowed("ADMIN")
    @GetMapping("/{userId}")
    @Operation(summary = "Анализ пользователя по его ID",
            description = "Отправляется запрос на эндпоинт, после чего сервис начинает сбор информации по пользователю и прогоняет данные через нейросеть")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное проведение анализа",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> analyzeUser(@PathVariable UUID userId) {
        return null;
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/{countOfUsers}")
    @Operation(summary = "Проведение анализа по N случайным пользователям",
            description = "Отправляется запрос на эндпоинт, после чего сервис начинает сбор информации по N случайным пользователям и прогоняет данные через нейросеть")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное проведение анализа",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> analyzeRandomNUsers(@PathVariable Integer countOfUsers) {
        return null;
    }
}
