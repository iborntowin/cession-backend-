package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.entities.ItemCategory;
import com.example.cessionappbackend.services.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class ItemCategoryController {
    private final ItemCategoryService itemCategoryService;

    @Autowired
    public ItemCategoryController(ItemCategoryService itemCategoryService) {
        this.itemCategoryService = itemCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<ItemCategory>> getAllCategories() {
        return ResponseEntity.ok(itemCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemCategory> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(itemCategoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<ItemCategory> createCategory(@RequestBody ItemCategory category) {
        return ResponseEntity.ok(itemCategoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemCategory> updateCategory(@PathVariable Long id, @RequestBody ItemCategory category) {
        return ResponseEntity.ok(itemCategoryService.updateCategory(id, category));
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        itemCategoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
} 