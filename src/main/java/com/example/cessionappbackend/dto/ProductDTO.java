package com.example.cessionappbackend.dto;

import com.example.cessionappbackend.entities.ItemCategory;
import com.example.cessionappbackend.entities.Product;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    
    @JsonProperty("sku")
    private String sku;

    @JsonProperty("category_id")
    private Long categoryId;
    
    private String supplier;

    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    @JsonProperty("reorder_point")
    private Integer reorderPoint;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("purchase_price")
    private BigDecimal purchasePrice;

    @JsonProperty("selling_price")
    private BigDecimal sellingPrice;
    
    private String specs;

    // Default constructor
    public ProductDTO() {}

    // Constructor from Product entity
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.sku = product.getSku();
        this.categoryId = product.getCategory() != null ? product.getCategory().getId() : null; // Populate categoryId
        this.supplier = product.getSupplier();
        this.stockQuantity = product.getStockQuantity();
        this.reorderPoint = product.getReorderPoint();
        this.imageUrl = product.getImageUrl();
        this.purchasePrice = product.getPurchasePrice();
        this.sellingPrice = product.getSellingPrice();
        this.specs = product.getSpecs();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    // Convert to Product entity
    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setSku(this.sku);
        // category will be set in the service layer based on categoryId
        product.setSupplier(this.supplier);
        product.setStockQuantity(this.stockQuantity);
        product.setReorderPoint(this.reorderPoint);
        product.setImageUrl(this.imageUrl);
        product.setPurchasePrice(this.purchasePrice);
        product.setSellingPrice(this.sellingPrice);
        product.setSpecs(this.specs);
        return product;
    }
} 