package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.FinancialSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.UUID;

@Service
public class FinancialSummaryService {
    private final ExpenseService expenseService;
    private final StockMovementService stockMovementService;

    @Autowired
    public FinancialSummaryService(ExpenseService expenseService, StockMovementService stockMovementService) {
        this.expenseService = expenseService;
        this.stockMovementService = stockMovementService;
    }

    public FinancialSummaryDTO getMonthlySummary(UUID userId, int year, int month) {
        // Get the start and end of the month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        // Get total income from product sales for the month
        BigDecimal totalIncome = stockMovementService.getTotalSalesIncome(startOfMonth, endOfMonth);

        // Get expenses by category
        Map<String, BigDecimal> expensesByCategory = expenseService.getExpensesByCategory(userId, year, month);

        // Calculate total expenses
        BigDecimal totalExpenses = expensesByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate profit
        BigDecimal profit = totalIncome.subtract(totalExpenses);

        // Create and return summary
        FinancialSummaryDTO summary = new FinancialSummaryDTO();
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpenses(totalExpenses);
        summary.setProfit(profit);
        summary.setExpensesByCategory(expensesByCategory);

        return summary;
    }
} 