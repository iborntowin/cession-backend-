package com.example.cessionappbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cessions", indexes = {
    @Index(name = "idx_cessions_client_id", columnList = "client_id"),
    @Index(name = "idx_cessions_status", columnList = "status"),
    @Index(name = "idx_cessions_start_date", columnList = "start_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cession {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false) // Mapped by client_id in schema
    private Client client;

    // Link to the specific contract document for this cession
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_document_id", unique = true)
    private Document contractDocument;

    @Column(name = "monthly_payment", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column // Can be null initially, might be set or calculated
    private LocalDate endDate;

    @Column // Auto-calculated
    private LocalDate expectedPayoffDate;

    @Column(precision = 12, scale = 2) // Auto-calculated
    private BigDecimal remainingBalance;
    
    @Column(precision = 12, scale = 2) // Total loan amount
    private BigDecimal totalLoanAmount;
    
    @Column(precision = 5, scale = 2) // Auto-calculated percentage of completion
    private BigDecimal currentProgress;
    
    @Column // Auto-calculated
    private Integer monthsRemaining;

    @Column(nullable = false)
    private String bankOrAgency;

    @Column(nullable = false, length = 50)
    private String status = "ACTIVE"; // Default status

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // New fields for PDF generation
    private String courtName;
    private String bookNumber;
    private String pageNumber;
    private String date;
    private String supplierTaxId;
    private String supplierName;
    private String supplierAddress;
    private String supplierBankAccount;
    private String itemDescription;
    private String amountInWords;
    private String loanDuration;
    private String firstDeductionMonthArabic;

    // Method to link the contract document (potentially called from service layer)
    public void setContractDocument(Document document) {
        this.contractDocument = document;
        if (document != null) {
            document.setCession(this); // Maintain bidirectional link if needed
        }
    }

    // Getter for total loan amount
    public BigDecimal getTotalLoanAmount() {
        return totalLoanAmount;
    }

    // Setter for total loan amount
    public void setTotalLoanAmount(BigDecimal totalLoanAmount) {
        this.totalLoanAmount = totalLoanAmount;
    }

    // Alias method for getTotalLoanAmount() to maintain compatibility
    public BigDecimal getTotalAmount() {
        return getTotalLoanAmount();
    }
}
