package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.StockMovement;
import com.example.cessionappbackend.entities.StockMovement.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductId(Long productId);
    
    List<StockMovement> findByType(MovementType type);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId AND sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> findByProductAndDateRange(Long productId, LocalDateTime startDate, LocalDateTime endDate);

    List<StockMovement> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT sm.* FROM stock_movements sm " +
           "JOIN product p ON sm.product_id = p.id " +
           "WHERE (:type IS NULL OR sm.type = :type) " +
           "ORDER BY sm.created_at DESC LIMIT :limit", nativeQuery = true)
    List<StockMovement> findRecentStockMovements(@Param("type") String type, @Param("limit") int limit);
} 