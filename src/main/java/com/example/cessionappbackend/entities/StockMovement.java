package com.example.cessionappbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "stock_movements")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "previous_quantity")
    private Integer previousQuantity;

    @Column(name = "new_quantity")
    private Integer newQuantity;

    @Column(name = "selling_price_at_sale", precision = 10, scale = 3)
    private BigDecimal sellingPriceAtSale;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum MovementType {
        INBOUND,
        OUTBOUND,
        ADJUSTMENT
    }
} 