package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, UUID> {
    boolean existsByName(String name);
} 