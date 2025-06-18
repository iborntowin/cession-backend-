package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.IncomeDTO;
import com.example.cessionappbackend.services.IncomeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @Autowired
    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IncomeDTO> createIncome(@Valid @RequestBody IncomeDTO incomeDTO) {
        return ResponseEntity.ok(incomeService.createIncome(incomeDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IncomeDTO>> getIncomesByMonth(
        @RequestParam UUID userId,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return ResponseEntity.ok(incomeService.getIncomesByMonth(userId, year, month));
    }

    @GetMapping("/range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<IncomeDTO>> getIncomesByDateRange(
        @RequestParam UUID userId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(incomeService.getIncomesByDateRange(userId, startDate, endDate, pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<IncomeDTO>> getAllIncomes(
        @RequestParam UUID userId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(incomeService.getAllIncomes(userId, pageable));
    }

    @GetMapping("/sources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Double>> getIncomesBySource(
        @RequestParam UUID userId,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return ResponseEntity.ok(incomeService.getIncomesBySourceForMonth(userId, year, month));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIncome(@PathVariable UUID id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.ok().build();
    }
} 