package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID>, JpaSpecificationExecutor<Client> {
    Optional<Client> findByCin(Integer cin);
    Optional<Client> findByClientNumber(Integer clientNumber);
    Optional<Client> findByFullName(String fullName);
    Optional<Client> findByWorkerNumber(Integer workerNumber);
    // Add custom query methods if needed, e.g., find by name containing
}
