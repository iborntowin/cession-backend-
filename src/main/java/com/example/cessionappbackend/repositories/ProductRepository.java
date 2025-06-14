package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Product;
import com.example.cessionappbackend.entities.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ItemCategory category);
    
    List<Product> findBySupplier(String supplier);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.reorderPoint")
    List<Product> findLowStockProducts();
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);

    List<Product> findByStockQuantityLessThanEqual(Integer reorderPoint);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
} 