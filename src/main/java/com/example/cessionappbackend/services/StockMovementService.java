package com.example.cessionappbackend.services;

import com.example.cessionappbackend.entities.StockMovement;
import com.example.cessionappbackend.entities.Product;
import com.example.cessionappbackend.repositories.StockMovementRepository;
import com.example.cessionappbackend.repositories.ProductRepository;
import com.example.cessionappbackend.dto.StockMovementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class StockMovementService {
    @Autowired
    private StockMovementRepository stockMovementRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }
    
    public List<StockMovement> getStockMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProductId(productId);
    }
    
    public List<StockMovement> getStockMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    public List<StockMovementDTO> getRecentStockMovements(String type, int limit) {
        try {
            // Convert string type to enum if provided
            StockMovement.MovementType movementType = null;
            if (type != null && !type.isEmpty()) {
                try {
                    movementType = StockMovement.MovementType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid movement type: " + type);
                }
            }
            
            Pageable pageable = PageRequest.of(0, limit);
            Page<StockMovement> movementsPage = stockMovementRepository.findRecentStockMovements(
                movementType,
                pageable
            );
            
            return movementsPage.getContent().stream()
                .map(movement -> {
                    StockMovementDTO dto = new StockMovementDTO();
                    dto.setId(movement.getId());
                    dto.setType(movement.getType());
                    dto.setQuantity(movement.getQuantity());
                    dto.setPreviousQuantity(movement.getPreviousQuantity());
                    dto.setNewQuantity(movement.getNewQuantity());
                    dto.setSellingPriceAtSale(movement.getSellingPriceAtSale());
                    dto.setNotes(movement.getNotes());
                    dto.setCreatedAt(movement.getCreatedAt());
                    
                    // Ensure product data is loaded and set
                    if (movement.getProduct() != null) {
                        dto.setProductId(movement.getProduct().getId());
                        dto.setProductName(movement.getProduct().getName());
                        dto.setPurchasePrice(movement.getProduct().getPurchasePrice());
                        
                        // Calculate profit for OUTBOUND movements
                        if (movement.getType() == StockMovement.MovementType.OUTBOUND && 
                            movement.getSellingPriceAtSale() != null && 
                            movement.getProduct().getPurchasePrice() != null) {
                            BigDecimal profit = movement.getSellingPriceAtSale()
                                .subtract(movement.getProduct().getPurchasePrice())
                                .multiply(BigDecimal.valueOf(Math.abs(movement.getQuantity())));
                            dto.setProfit(profit);
                        } else {
                            dto.setProfit(BigDecimal.ZERO);
                        }
                    } else {
                        dto.setProductName("Unknown Product");
                        dto.setProfit(BigDecimal.ZERO);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching recent stock movements: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public StockMovementDTO recordStockMovement(Long productId, int quantity, BigDecimal sellingPrice, String notes) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        int previousQuantity = product.getStockQuantity();
        int newQuantity;
        StockMovement.MovementType movementType;

        // Determine movement type and calculate new quantity
        if (quantity < 0) {
            movementType = StockMovement.MovementType.OUTBOUND;
            newQuantity = previousQuantity + quantity; // quantity is negative for outbound
        } else if (quantity > 0) {
            movementType = StockMovement.MovementType.INBOUND;
            newQuantity = previousQuantity + quantity;
        } else {
            throw new IllegalArgumentException("Quantity cannot be zero.");
        }

        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock for this sale.");
        }

        product.setStockQuantity(newQuantity);
        productRepository.save(product);
        
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setType(movementType);
        movement.setQuantity(Math.abs(quantity));
        movement.setPreviousQuantity(previousQuantity);
        movement.setNewQuantity(newQuantity);
        movement.setNotes(notes);
        movement.setCreatedAt(LocalDateTime.now());
        movement.setSellingPriceAtSale(sellingPrice);
        
        StockMovement savedMovement = stockMovementRepository.save(movement);
        return convertToDTO(savedMovement);
    }

    private StockMovementDTO convertToDTO(StockMovement movement) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        if (movement.getProduct() != null) {
            dto.setProductId(movement.getProduct().getId());
            dto.setProductName(movement.getProduct().getName());
            dto.setPurchasePrice(movement.getProduct().getPurchasePrice());
            
            // Calculate profit for OUTBOUND movements
            if (movement.getType() == StockMovement.MovementType.OUTBOUND && 
                movement.getSellingPriceAtSale() != null && 
                movement.getProduct().getPurchasePrice() != null) {
                BigDecimal profit = movement.getSellingPriceAtSale()
                    .subtract(movement.getProduct().getPurchasePrice())
                    .multiply(BigDecimal.valueOf(movement.getQuantity()));
                dto.setProfit(profit);
            }
        }
        dto.setType(movement.getType());
        dto.setQuantity(movement.getQuantity());
        dto.setPreviousQuantity(movement.getPreviousQuantity());
        dto.setNewQuantity(movement.getNewQuantity());
        dto.setNotes(movement.getNotes());
        dto.setCreatedAt(movement.getCreatedAt());
        dto.setSellingPriceAtSale(movement.getSellingPriceAtSale());
        return dto;
    }

    public BigDecimal getTotalSalesIncome(LocalDateTime startDate, LocalDateTime endDate) {
        List<StockMovement> sales = stockMovementRepository.findByCreatedAtBetween(startDate, endDate);
        return sales.stream()
            .filter(movement -> movement.getType() == StockMovement.MovementType.OUTBOUND)
            .map(movement -> {
                if (movement.getSellingPriceAtSale() != null) {
                    return movement.getSellingPriceAtSale()
                        .multiply(BigDecimal.valueOf(movement.getQuantity()));
                }
                return BigDecimal.ZERO;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 