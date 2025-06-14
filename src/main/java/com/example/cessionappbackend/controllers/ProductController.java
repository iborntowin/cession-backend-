package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.ProductDTO;
import com.example.cessionappbackend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        logger.info("Received request to get all products");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        logger.info("Received request to get product by ID: {}", id);
        Optional<ProductDTO> productOptional = productService.getProductById(id);
        return productOptional.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        logger.info("Received request to create product: {}", productDTO.getName());
        logger.info("Product SKU: {}", productDTO.getSku());
        logger.info("Product Category ID: {}", productDTO.getCategoryId());
        logger.info("Product Price: {}", productDTO.getReorderPoint());
        logger.info("Product Stock Quantity: {}", productDTO.getStockQuantity());
        logger.info("Product Reorder Point: {}", productDTO.getReorderPoint());
        logger.info("Product Purchase Price: {}", productDTO.getPurchasePrice());
        logger.info("Product Selling Price: {}", productDTO.getSellingPrice());
        logger.info("Product Supplier: {}", productDTO.getSupplier());
        logger.info("Product Description: {}", productDTO.getDescription());
        logger.info("Product Specs: {}", productDTO.getSpecs());
        logger.info("Product Image URL: {}", productDTO.getImageUrl());

        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok(createdProduct);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        logger.info("Received request to update product with ID: {}", id);
        logger.info("Updated Product Name: {}", productDTO.getName());
        logger.info("Updated Product Stock Quantity: {}", productDTO.getStockQuantity());
        logger.info("Updated Product Reorder Point: {}", productDTO.getReorderPoint());

        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Received request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(
        @PathVariable Long id,
        @RequestParam int quantity,
        @RequestParam(required = false) String notes
    ) {
        logger.info("Received request to update stock for product {}: quantity={}, notes={}", id, quantity, notes);
        return ResponseEntity.ok(productService.updateStock(id, quantity, notes));
    }
    
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        logger.info("Received request to get low stock products");
        List<ProductDTO> lowStockProducts = productService.getLowStockProducts();
        return ResponseEntity.ok(lowStockProducts);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
        logger.info("Received request to search products with query: {}", query);
        List<ProductDTO> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }
} 