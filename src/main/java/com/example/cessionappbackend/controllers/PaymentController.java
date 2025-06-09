package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.PaymentDTO;
import com.example.cessionappbackend.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(paymentService.createPayment(paymentDTO));
    }

    @GetMapping("/cession/{cessionId}")
    public ResponseEntity<List<PaymentDTO>> getCessionPayments(@PathVariable UUID cessionId) {
        if (cessionId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(paymentService.getCessionPayments(cessionId));
    }

    @GetMapping("/cession/{cessionId}/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDateRange(
            @PathVariable UUID cessionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (cessionId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(paymentService.getPaymentsByDateRange(cessionId, startDate, endDate));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}