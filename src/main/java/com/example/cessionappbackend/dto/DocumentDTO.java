package com.example.cessionappbackend.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class DocumentDTO {
    private UUID id;
    private UUID clientId;
    private UUID cessionId; // Nullable
    private String documentType; // e.g., NATIONAL_ID, JOB_CARD, CESSION_CONTRACT
    private String fileName;
    private String storagePath; // Usually not exposed directly, maybe a download URL?
    private String mimeType;
    private OffsetDateTime uploadedAt;
    private String downloadUrl; // Add a field for a temporary download URL if needed
}

