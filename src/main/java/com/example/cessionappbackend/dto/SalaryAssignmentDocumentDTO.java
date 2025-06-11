package com.example.cessionappbackend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SalaryAssignmentDocumentDTO {
    // Court Reference
    private String courtName;
    private String bookNumber;
    private String pageNumber;
    private String date;

    // Supplier Information
    private String supplierTaxId;
    private String supplierName;
    private String supplierAddress;
    private String supplierBankAccount;

    // Worker Information
    private String workerNumber;
    private String fullName;
    private String cin;
    private String address;
    private String workplace;
    private String jobTitle;
    private String employmentStatus;
    private String bankAccountNumber;

    // Purchase Information
    private String itemDescription;
    private String amountInWords;
    private Double totalAmountNumeric;
    private Double monthlyPayment;
    private String loanDuration;
    private String firstDeductionMonthArabic;
} 