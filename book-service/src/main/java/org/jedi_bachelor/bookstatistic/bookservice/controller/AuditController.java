package org.jedi_bachelor.bookstatistic.bookservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.service.AuditService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.audit.AuditRevisionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/books/audit")
public class AuditController {
    private final AuditService auditService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAuditInfo(@RequestParam("count") boolean desc) {
        List<AuditRevisionDto> dtos = auditService.getUserAuditHistory(desc);

        return ResponseEntity.ok(dtos);
    }
}
