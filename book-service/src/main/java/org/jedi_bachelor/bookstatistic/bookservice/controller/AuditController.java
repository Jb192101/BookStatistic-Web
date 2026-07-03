package org.jedi_bachelor.bookstatistic.bookservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.service.AuditService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.audit.AuditRevisionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/books/audit")
@Slf4j
public class AuditController {
    private final AuditService auditService;

    // ROLE_ADMIN, ROLE_MODERATOR
    @GetMapping
    public ResponseEntity<?> getAuditInfo(@RequestParam("desc") boolean desc) {
        List<AuditRevisionDto> dtos = this.auditService.getUserAuditHistory(desc);

        return ResponseEntity.ok(dtos);
    }
}
