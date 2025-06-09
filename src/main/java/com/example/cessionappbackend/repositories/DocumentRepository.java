package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByClientId(UUID clientId);
    List<Document> findByCessionId(UUID cessionId);
    Optional<Document> findByStoragePath(String storagePath);
    // Find documents by client and type (e.g., find NATIONAL_ID for a client)
    Optional<Document> findByClientIdAndDocumentType(UUID clientId, String documentType);
}

