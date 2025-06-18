package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.ExpenseDTO;
import com.example.cessionappbackend.entities.Expense;
import com.example.cessionappbackend.entities.ExpenseCategory;
import com.example.cessionappbackend.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        Expense expense = new Expense();
        expense.setCategory(ExpenseCategory.valueOf(expenseDTO.getCategory().toString()));
        expense.setLabel(expenseDTO.getLabel());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate());
        expense.setDescription(expenseDTO.getDescription());
        expense.setUserId(expenseDTO.getUserId());

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDTO(savedExpense);
    }

    public List<ExpenseDTO> getExpensesByMonth(UUID userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getExpensesByCategory(UUID userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Map<String, Object>> results = expenseRepository.getExpensesByCategoryForPeriod(userId, startDate, endDate);
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();

        for (Map<String, Object> result : results) {
            String category = result.get("category").toString();
            BigDecimal total = new BigDecimal(result.get("total").toString());
            expensesByCategory.put(category, total);
        }

        return expensesByCategory;
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setCategory(expense.getCategory());
        dto.setLabel(expense.getLabel());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setDescription(expense.getDescription());
        dto.setUserId(expense.getUserId());
        return dto;
    }

    public Page<ExpenseDTO> getExpensesByDateRange(UUID userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return expenseRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable)
                .map(this::convertToDTO);
    }

    public Page<ExpenseDTO> getAllExpenses(UUID userId, Pageable pageable) {
        return expenseRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    public Map<String, Double> getExpensesByCategoryForMonth(UUID userId, int year, int month) {
        List<Object[]> results = expenseRepository.getExpensesByCategoryForMonth(userId, year, month);
        return results.stream()
            .collect(Collectors.toMap(
                row -> row[0].toString(),
                row -> ((Number) row[1]).doubleValue()
            ));
    }

    @Transactional
    public void deleteExpense(UUID id) {
        expenseRepository.deleteById(id);
    }

    @Transactional
    public ExpenseDTO updateExpense(UUID id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        expense.setCategory(ExpenseCategory.valueOf(expenseDTO.getCategory().toString()));
        expense.setLabel(expenseDTO.getLabel());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate());
        expense.setDescription(expenseDTO.getDescription());
        
        Expense updatedExpense = expenseRepository.save(expense);
        return convertToDTO(updatedExpense);
    }
} 