package com.example.cessionappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CessionDTO {
    private UUID id;
    private UUID clientId;
    private String clientName;
    private Integer clientNumber;
    private Integer clientCin;
    private UUID contractDocumentId;
    private String contractDocumentName;
    private BigDecimal totalLoanAmount;
    private BigDecimal monthlyPayment;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate expectedPayoffDate;
    private BigDecimal remainingBalance;
    private BigDecimal currentProgress;
    private Integer monthsRemaining;
    private String bankOrAgency;
    private String status;
    
    // Job information
    private UUID jobId;
    private String jobName;
}
