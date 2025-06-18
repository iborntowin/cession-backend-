package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByUserIdAndDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    Page<Expense> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    List<Expense> findByUserIdAndYearAndMonth(
        @Param("userId") UUID userId,
        @Param("year") int year,
        @Param("month") int month
    );

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.date BETWEEN :startDate AND :endDate ORDER BY e.date DESC")
    Page<Expense> findByUserIdAndDateRange(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.userId = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month GROUP BY e.category")
    List<Object[]> getExpensesByCategoryForMonth(
        @Param("userId") UUID userId,
        @Param("year") int year,
        @Param("month") int month
    );

    @Query("SELECT e.category as category, SUM(e.amount) as total " +
           "FROM Expense e " +
           "WHERE e.userId = :userId AND e.date BETWEEN :startDate AND :endDate " +
           "GROUP BY e.category")
    List<Map<String, Object>> getExpensesByCategoryForPeriod(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
} 