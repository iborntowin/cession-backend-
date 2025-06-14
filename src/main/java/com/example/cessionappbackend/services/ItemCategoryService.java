package com.example.cessionappbackend.services;

import com.example.cessionappbackend.entities.ItemCategory;
import com.example.cessionappbackend.repositories.ItemCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemCategoryService {
    private final ItemCategoryRepository itemCategoryRepository;

    @Autowired
    public ItemCategoryService(ItemCategoryRepository itemCategoryRepository) {
        this.itemCategoryRepository = itemCategoryRepository;
    }

    public List<ItemCategory> getAllCategories() {
        return itemCategoryRepository.findAll();
    }

    public ItemCategory getCategoryById(Long id) {
        return itemCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public ItemCategory createCategory(ItemCategory category) {
        return itemCategoryRepository.save(category);
    }

    public ItemCategory updateCategory(Long id, ItemCategory category) {
        ItemCategory existingCategory = getCategoryById(id);
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        return itemCategoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        itemCategoryRepository.deleteById(id);
    }
} 