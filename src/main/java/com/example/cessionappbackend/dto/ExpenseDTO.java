package com.example.cessionappbackend.dto;

import com.example.cessionappbackend.entities.ExpenseCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExpenseDTO {
    private UUID id;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Category is required")
    private ExpenseCategory category;

    @NotBlank(message = "Label is required")
    @Size(max = 255, message = "Label must be less than 255 characters")
    private String label;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Amount must be less than 1,000,000")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date must be in the past or present")
    private LocalDate date;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
} 