package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IncomeRepository extends JpaRepository<Income, UUID> {
    List<Income> findByUserIdAndDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
    
    Page<Income> findByUserId(UUID userId, Pageable pageable);
    
    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND YEAR(i.date) = :year AND MONTH(i.date) = :month")
    List<Income> findByUserIdAndYearAndMonth(
        @Param("userId") UUID userId,
        @Param("year") int year,
        @Param("month") int month
    );
    
    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND i.date BETWEEN :startDate AND :endDate ORDER BY i.date DESC")
    Page<Income> findByUserIdAndDateRange(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    
    @Query("SELECT i.source, SUM(i.amount) FROM Income i WHERE i.userId = :userId AND YEAR(i.date) = :year AND MONTH(i.date) = :month GROUP BY i.source")
    List<Object[]> getIncomesBySourceForMonth(
        @Param("userId") UUID userId,
        @Param("year") int year,
        @Param("month") int month
    );

    @Query("SELECT SUM(i.amount) FROM Income i " +
           "WHERE i.userId = :userId AND i.date BETWEEN :startDate AND :endDate")
    Double getTotalIncomeForPeriod(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
} 