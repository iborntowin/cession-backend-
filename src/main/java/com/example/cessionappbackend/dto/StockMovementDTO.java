package com.example.cessionappbackend.dto;

import com.example.cessionappbackend.entities.StockMovement.MovementType;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class StockMovementDTO {
    private Long id;
    private Long productId;
    private String productName;
    private MovementType type;
    private Integer quantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private BigDecimal sellingPriceAtSale;
    private BigDecimal purchasePrice; // From product
    private BigDecimal profit; // Calculated field
    private String notes;
    private LocalDateTime createdAt;
} 