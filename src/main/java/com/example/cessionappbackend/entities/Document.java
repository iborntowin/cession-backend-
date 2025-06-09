package com.example.cessionappbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_documents_client_id", columnList = "client_id"),
    @Index(name = "idx_documents_cession_id", columnList = "cession_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id") // Mapped by client_id in schema
    private Client client;

    // Nullable because not all documents belong to a cession (e.g., ID card)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cession_id", nullable = true) // Mapped by cession_id in schema
    private Cession cession;

    @Column(nullable = false, length = 50)
    private String documentType; // e.g., NATIONAL_ID, JOB_CARD, CESSION_CONTRACT

    @Column(nullable = false)
    private String fileName; // Original file name

    @Column(nullable = false, length = 1024, unique = true)
    private String storagePath; // Path/key in cloud storage

    @Column(nullable = false, length = 100)
    private String mimeType = "application/pdf";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime uploadedAt;

    // Note: No updated_at needed here as documents are typically immutable once uploaded.
}

