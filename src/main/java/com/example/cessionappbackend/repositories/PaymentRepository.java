package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    List<Payment> findByCessionIdOrderByPaymentDateDesc(UUID cessionId);
    
    @Query("SELECT p FROM Payment p WHERE p.cession.id = :cessionId AND p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsByCessionAndDateRange(
        @Param("cessionId") UUID cessionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.cession.id = :cessionId")
    BigDecimal getTotalPaymentsByCession(@Param("cessionId") UUID cessionId);
}