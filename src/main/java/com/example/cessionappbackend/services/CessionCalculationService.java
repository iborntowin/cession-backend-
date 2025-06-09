package com.example.cessionappbackend.services;

import com.example.cessionappbackend.entities.Cession;
import com.example.cessionappbackend.repositories.CessionRepository;
import com.example.cessionappbackend.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class CessionCalculationService {

    @Autowired
    private CessionRepository cessionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Recalculate cession fields after a new payment is made
     */
    @Transactional
    public Cession recalculateCessionAfterPayment(Cession cession) {
        // Calculate remaining balance
        BigDecimal remainingBalance = calculateRemainingBalance(cession);
        cession.setRemainingBalance(remainingBalance);
        
        // Calculate current progress
        BigDecimal currentProgress = calculateCurrentProgress(cession);
        cession.setCurrentProgress(currentProgress);
        
        // Calculate months remaining
        int monthsRemaining = calculateMonthsRemaining(cession);
        cession.setMonthsRemaining(monthsRemaining);
        
        // Update status if fully paid
        if (remainingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            cession.setStatus("FINISHED");
        }
        
        // Save and return updated cession
        return cessionRepository.save(cession);
    }

    /**
     * Calculate and update all derived fields for a cession
     * - expectedPayoffDate
     * - remainingBalance
     * - currentProgress
     * - monthsRemaining
     */
    public Cession calculateAndUpdateCessionFields(Cession cession) {
        // Calculate total months needed for repayment
        int totalMonthsNeeded = calculateTotalMonthsNeeded(cession);
        
        // Calculate expected payoff date
        LocalDate expectedPayoffDate = cession.getStartDate().plusMonths(totalMonthsNeeded);
        cession.setExpectedPayoffDate(expectedPayoffDate);
        
        // Calculate remaining balance
        BigDecimal remainingBalance = calculateRemainingBalance(cession);
        cession.setRemainingBalance(remainingBalance);
        
        // Calculate current progress percentage
        BigDecimal currentProgress = calculateCurrentProgress(cession);
        cession.setCurrentProgress(currentProgress);
        
        // Calculate months remaining
        int monthsRemaining = calculateMonthsRemaining(cession);
        cession.setMonthsRemaining(monthsRemaining);
        
        // Save and return updated cession
        return cessionRepository.save(cession);
    }
    
    /**
     * Calculate total months needed to repay the cession
     */
    public int calculateTotalMonthsNeeded(Cession cession) {
        if (cession.getMonthlyPayment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly payment must be greater than zero");
        }
        
        // Calculate total months needed (ceiling to ensure full repayment)
        BigDecimal totalMonths = cession.getTotalLoanAmount().divide(cession.getMonthlyPayment(), 0, RoundingMode.CEILING);
        return totalMonths.intValue();
    }
    
    /**
     * Calculate remaining balance based on current date and payment history
     */
    public BigDecimal calculateRemainingBalance(Cession cession) {
        // Get total payments made
        BigDecimal totalPaid = paymentRepository.getTotalPaymentsByCession(cession.getId());
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }
        
        // Calculate remaining balance (ensure it doesn't go negative)
        BigDecimal remainingBalance = cession.getTotalLoanAmount().subtract(totalPaid);
        return remainingBalance.compareTo(BigDecimal.ZERO) > 0 ? remainingBalance : BigDecimal.ZERO;
    }
    
    /**
     * Calculate current progress as a percentage
     */
    public BigDecimal calculateCurrentProgress(Cession cession) {
        BigDecimal totalPaid = paymentRepository.getTotalPaymentsByCession(cession.getId());
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }
        
        if (cession.getTotalLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        return totalPaid.multiply(new BigDecimal("100"))
                .divide(cession.getTotalLoanAmount(), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate months remaining based on current progress and monthly payment
     */
    public int calculateMonthsRemaining(Cession cession) {
        BigDecimal remainingBalance = calculateRemainingBalance(cession);
        if (remainingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        
        if (cession.getMonthlyPayment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly payment must be greater than zero");
        }
        
        // Calculate months remaining (ceiling to ensure full repayment)
        BigDecimal monthsRemaining = remainingBalance.divide(cession.getMonthlyPayment(), 0, RoundingMode.CEILING);
        return monthsRemaining.intValue();
    }
    
    /**
     * Update calculations for a specific cession by ID
     */
    public Optional<Cession> updateCalculationsById(UUID cessionId) {
        Optional<Cession> cessionOpt = cessionRepository.findById(cessionId);
        
        if (cessionOpt.isPresent()) {
            Cession cession = cessionOpt.get();
            return Optional.of(calculateAndUpdateCessionFields(cession));
        }
        
        return Optional.empty();
    }
    
    /**
     * Update calculations for all active cessions
     */
    public void updateCalculationsForAllActiveCessions() {
        cessionRepository.findByStatus("ACTIVE").forEach(this::calculateAndUpdateCessionFields);
    }
}
