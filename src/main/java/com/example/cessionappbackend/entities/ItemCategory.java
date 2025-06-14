package com.example.cessionappbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "item_category")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ItemCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
} 