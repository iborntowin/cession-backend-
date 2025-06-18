package com.example.cessionappbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class FinancialSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal profit;
    private Map<String, BigDecimal> expensesByCategory;
} 