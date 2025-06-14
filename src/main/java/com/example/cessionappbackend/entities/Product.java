package com.example.cessionappbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "product")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "sku")
    private String sku;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ItemCategory category;

    private String supplier;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "reorder_point")
    private Integer reorderPoint;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "purchase_price", precision = 10, scale = 3)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", precision = 10, scale = 3)
    private BigDecimal sellingPrice;

    @Column(length = 1000)
    private String specs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<StockMovement> stockMovements;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 