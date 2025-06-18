package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.ExpenseDTO;
import com.example.cessionappbackend.services.ExpenseService;
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
@RequestMapping("/api/v1/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        return ResponseEntity.ok(expenseService.createExpense(expenseDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByMonth(
        @RequestParam UUID userId,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return ResponseEntity.ok(expenseService.getExpensesByMonth(userId, year, month));
    }

    @GetMapping("/range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByDateRange(
        @RequestParam UUID userId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(expenseService.getExpensesByDateRange(userId, startDate, endDate, pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ExpenseDTO>> getAllExpenses(
        @RequestParam UUID userId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(expenseService.getAllExpenses(userId, pageable));
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Double>> getExpensesByCategory(
        @RequestParam UUID userId,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return ResponseEntity.ok(expenseService.getExpensesByCategoryForMonth(userId, year, month));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExpenseDTO> updateExpense(
        @PathVariable UUID id,
        @Valid @RequestBody ExpenseDTO expenseDTO
    ) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expenseDTO));
    }
} 