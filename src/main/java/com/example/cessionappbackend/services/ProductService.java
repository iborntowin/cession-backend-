package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.ProductDTO;
import com.example.cessionappbackend.entities.Product;
import com.example.cessionappbackend.entities.ItemCategory;
import com.example.cessionappbackend.repositories.ProductRepository;
import com.example.cessionappbackend.repositories.ItemCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemCategoryRepository categoryRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .map(ProductDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id)
            .map(ProductDTO::new);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productDTO.toEntity();
        if (productDTO.getCategoryId() != null) {
            ItemCategory category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productDTO.getCategoryId()));
            product.setCategory(category);
        }
        return new ProductDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setSku(productDTO.getSku());
        
        if (productDTO.getCategoryId() != null) {
            ItemCategory category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productDTO.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null); // Allow setting category to null if categoryId is null
        }
        
        product.setSupplier(productDTO.getSupplier());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setReorderPoint(productDTO.getReorderPoint());
        product.setImageUrl(productDTO.getImageUrl());
        product.setPurchasePrice(productDTO.getPurchasePrice());
        product.setSellingPrice(productDTO.getSellingPrice());
        product.setSpecs(productDTO.getSpecs());

        return new ProductDTO(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductDTO updateStock(Long id, int quantity, String notes) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        int previousQuantity = product.getStockQuantity();
        int newQuantity = previousQuantity + quantity;
        
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        product.setStockQuantity(newQuantity);
        return new ProductDTO(productRepository.save(product));
    }

    public List<ProductDTO> getLowStockProducts() {
        return productRepository.findByStockQuantityLessThanEqual(0).stream()
            .map(ProductDTO::new)
            .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query).stream()
            .map(ProductDTO::new)
            .collect(Collectors.toList());
    }
} 