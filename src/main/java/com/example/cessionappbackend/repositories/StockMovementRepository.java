package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.StockMovement;
import com.example.cessionappbackend.entities.StockMovement.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductId(Long productId);
    
    List<StockMovement> findByType(MovementType type);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId AND sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> findByProductAndDateRange(Long productId, LocalDateTime startDate, LocalDateTime endDate);

    List<StockMovement> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT sm FROM StockMovement sm LEFT JOIN FETCH sm.product p WHERE (:type IS NULL OR sm.type = :type) ORDER BY sm.createdAt DESC")
    Page<StockMovement> findRecentStockMovements(@Param("type") MovementType type, Pageable pageable);
} 