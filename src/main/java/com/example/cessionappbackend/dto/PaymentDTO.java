package com.example.cessionappbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PaymentDTO {
    private UUID id;
    private UUID cessionId;
    private String cessionClientName;  // For display purposes
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String notes;
    
    // Additional fields for UI display
    private BigDecimal remainingBalanceAfterPayment;
    private BigDecimal updatedProgress;
}