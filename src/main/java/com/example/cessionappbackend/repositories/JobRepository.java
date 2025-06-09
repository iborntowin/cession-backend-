package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Job;
import com.example.cessionappbackend.entities.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    Optional<Job> findByName(String name);
    List<Job> findByWorkplaceId(UUID workplaceId);
    boolean existsByNameAndWorkplace(String name, Workplace workplace);
    boolean existsByName(String name);
} 