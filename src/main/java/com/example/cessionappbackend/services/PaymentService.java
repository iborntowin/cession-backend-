package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.PaymentDTO;
import com.example.cessionappbackend.entities.Cession;
import com.example.cessionappbackend.entities.Payment;
import com.example.cessionappbackend.repositories.CessionRepository;
import com.example.cessionappbackend.repositories.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CessionRepository cessionRepository;
    private final CessionCalculationService calculationService;

    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Cession cession = cessionRepository.findById(paymentDTO.getCessionId())
                .orElseThrow(() -> new EntityNotFoundException("Cession not found"));

        Payment payment = new Payment();
        payment.setCession(cession);
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setNotes(paymentDTO.getNotes());

        payment = paymentRepository.save(payment);
        
        // Update cession calculations after payment
        calculationService.recalculateCessionAfterPayment(cession);
        cessionRepository.save(cession);

        return convertToDTO(payment, cession);
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getCessionPayments(UUID cessionId) {
        Cession cession = cessionRepository.findById(cessionId)
                .orElseThrow(() -> new EntityNotFoundException("Cession not found"));

        return paymentRepository.findByCessionIdOrderByPaymentDateDesc(cessionId).stream()
                .map(payment -> convertToDTO(payment, cession))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByDateRange(UUID cessionId, LocalDate startDate, LocalDate endDate) {
        Cession cession = cessionRepository.findById(cessionId)
                .orElseThrow(() -> new EntityNotFoundException("Cession not found"));

        return paymentRepository.findPaymentsByCessionAndDateRange(cessionId, startDate, endDate).stream()
                .map(payment -> convertToDTO(payment, cession))
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
            .map(payment -> {
                PaymentDTO dto = convertToDTO(payment);
                Optional<Cession> cession = cessionRepository.findById(payment.getCession().getId());
                cession.ifPresent(c -> {
                    dto.setCessionId(c.getId());
                    dto.setCessionClientName(c.getClient().getFullName());
                });
                return dto;
            })
            .collect(Collectors.toList());
    }

    private PaymentDTO convertToDTO(Payment payment, Cession cession) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setCessionId(cession.getId());
        dto.setCessionClientName(cession.getClient().getFullName());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setNotes(payment.getNotes());
        
        // Calculate remaining balance and progress
        BigDecimal totalPaid = paymentRepository.getTotalPaymentsByCession(cession.getId());
        BigDecimal remainingBalance = cession.getTotalLoanAmount().subtract(totalPaid);
        BigDecimal progress = totalPaid.multiply(new BigDecimal("100"))
                .divide(cession.getTotalLoanAmount(), 2, RoundingMode.HALF_UP);
        
        dto.setRemainingBalanceAfterPayment(remainingBalance);
        dto.setUpdatedProgress(progress);
        
        return dto;
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setNotes(payment.getNotes());
        return dto;
    }
}