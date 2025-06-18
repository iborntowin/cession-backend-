package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.FinancialSummaryDTO;
import com.example.cessionappbackend.services.FinancialSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial")
public class FinancialSummaryController {
    private final FinancialSummaryService financialSummaryService;

    @Autowired
    public FinancialSummaryController(FinancialSummaryService financialSummaryService) {
        this.financialSummaryService = financialSummaryService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialSummaryDTO> getMonthlySummary(
            @RequestParam UUID userId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(financialSummaryService.getMonthlySummary(userId, year, month));
    }
} 