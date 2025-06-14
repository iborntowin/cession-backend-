package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.entities.StockMovement;
import com.example.cessionappbackend.services.StockMovementService;
import com.example.cessionappbackend.dto.StockMovementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/stock-movements")
public class StockMovementController {
    @Autowired
    private StockMovementService stockMovementService;
    
    @GetMapping
    public ResponseEntity<List<StockMovement>> getAllStockMovements() {
        return ResponseEntity.ok(stockMovementService.getAllStockMovements());
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovement>> getStockMovementsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockMovementService.getStockMovementsByProduct(productId));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<StockMovement>> getStockMovementsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return ResponseEntity.ok(stockMovementService.getStockMovementsByDateRange(startDate, endDate));
    }
    
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentStockMovements(
        @RequestParam(required = false) String type,
        @RequestParam(defaultValue = "5") int limit
    ) {
        try {
            return ResponseEntity.ok(stockMovementService.getRecentStockMovements(type, limit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/record")
    public ResponseEntity<?> recordStockMovement(
        @RequestParam Long productId,
        @RequestParam int quantity,
        @RequestParam BigDecimal sellingPrice,
        @RequestParam(required = false) String notes
    ) {
        try {
            StockMovementDTO result = stockMovementService.recordStockMovement(productId, quantity, sellingPrice, notes);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 