package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.DocumentDTO;
import com.example.cessionappbackend.entities.Client;
import com.example.cessionappbackend.entities.Cession;
import com.example.cessionappbackend.entities.Document;
import com.example.cessionappbackend.repositories.CessionRepository;
import com.example.cessionappbackend.repositories.ClientRepository;
import com.example.cessionappbackend.repositories.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CessionRepository cessionRepository;

    @Autowired
    private StorageService storageService; // Custom service for Supabase Storage integration

    @Value("${app.document.allowed-types}")
    private List<String> allowedMimeTypes; // Configured in application.properties/yml

    // --- DTO Conversion --- //
    private DocumentDTO convertToDto(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setClientId(document.getClient().getId());
        dto.setCessionId(document.getCession() != null ? document.getCession().getId() : null);
        dto.setDocumentType(document.getDocumentType());
        dto.setFileName(document.getFileName());
        dto.setStoragePath(document.getStoragePath());
        dto.setMimeType(document.getMimeType());
        dto.setUploadedAt(document.getUploadedAt());
        
        // Generate a temporary download URL if needed
        try {
            // Pass the correct bucket name to generateDownloadUrl
            String targetBucket = storageService.getDefaultBucketName();
            if ("NATIONAL_ID".equals(document.getDocumentType()) || "JOB_CARD".equals(document.getDocumentType())) {
                targetBucket = storageService.getDocumentsBucketIdJob();
            }
            dto.setDownloadUrl(storageService.generateDownloadUrl(document.getStoragePath(), targetBucket));
        } catch (Exception e) {
            System.err.println("Error generating download URL for document " + document.getId() + ": " + e.getMessage());
            dto.setDownloadUrl(null); // Set to null if URL generation fails
        }
        
        return dto;
    }

    // --- CRUD Operations --- //

    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByClientId(UUID clientId) {
        return documentRepository.findByClientId(clientId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByCessionId(UUID cessionId) {
        return documentRepository.findByCessionId(cessionId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<DocumentDTO> getDocumentById(UUID id) {
        return documentRepository.findById(id).map(this::convertToDto);
    }

    @Transactional
    public DocumentDTO uploadClientDocument(UUID clientId, Integer clientNumber, String documentType, MultipartFile file) throws IOException {
        System.out.println("=== Document Upload Process Start ===");
        System.out.println("Client ID: " + clientId);
        System.out.println("Client Number: " + clientNumber);
        System.out.println("Document Type: " + documentType);
        System.out.println("File Name: " + file.getOriginalFilename());
        System.out.println("File Size: " + file.getSize() + " bytes");
        System.out.println("File Content Type: " + file.getContentType());

        // Validate client exists
        System.out.println("Validating client existence...");
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        System.out.println("Client found: " + client.getFullName());

        // Validate file type
        System.out.println("Validating file type...");
        validateFileType(file);
        System.out.println("File type validation passed");

        // Process document based on type
        String targetBucket;
        String uploadPath;
        String fileName;

        if (documentType.equals("NATIONAL_ID") || documentType.equals("JOB_CARD")) {
            System.out.println("Processing ID/Job Card document...");
            targetBucket = "id-job-documents";
            
            // Get original file extension
            String originalFileName = file.getOriginalFilename();
            String extension = ".pdf"; // Default extension
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            // Construct file name using client number
            fileName = clientNumber + extension;
            System.out.println("Original file name: " + originalFileName);
            System.out.println("Using extension: " + extension);
            System.out.println("Final file name: " + fileName);
            
            // Set upload path based on document type
            uploadPath = documentType.equals("NATIONAL_ID") ? "id-cards" : "job-cards";
            System.out.println("Upload path: " + uploadPath);
        } else if (documentType.equals("CESSION")) {
            System.out.println("Processing Cession document...");
            targetBucket = "cessions";
            
            // Get original file extension
            String originalFileName = file.getOriginalFilename();
            String extension = ".pdf"; // Default extension
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            // Construct file name using client number
            fileName = clientNumber + extension;
            System.out.println("Original file name: " + originalFileName);
            System.out.println("Using extension: " + extension);
            System.out.println("Final file name: " + fileName);
            
            // Set upload path
            uploadPath = "cessions";
            System.out.println("Upload path: " + uploadPath);
        } else {
            throw new IllegalArgumentException("Unsupported document type: " + documentType);
        }

        // Upload file to storage
        String storagePath = storageService.uploadFile(file, targetBucket, uploadPath + "/" + fileName);
        System.out.println("File uploaded successfully. Storage path: " + storagePath);

        // Create and save document entity
        System.out.println("Creating document entity...");
        Document document = new Document();
        document.setClient(client);
        document.setDocumentType(documentType);
        document.setFileName(fileName);
        document.setMimeType(file.getContentType());
        document.setStoragePath(storagePath);
        document.setUploadedAt(OffsetDateTime.now(ZoneOffset.UTC));

        System.out.println("Saving document to database...");
        Document savedDocument = documentRepository.save(document);
        System.out.println("Document saved with ID: " + savedDocument.getId());

        // Generate download URL
        try {
            String downloadUrl = storageService.generateDownloadUrl(storagePath, targetBucket);
            System.out.println("Download URL generated: " + downloadUrl);
        } catch (Exception e) {
            System.err.println("Error generating download URL for document " + savedDocument.getId() + ": " + e.getMessage());
        }

        System.out.println("=== Document Upload Process Complete ===");
        return convertToDto(savedDocument);
    }

    @Transactional
    public DocumentDTO uploadCessionDocument(UUID cessionId, MultipartFile file) throws IOException {
        // Validate cession exists
        Cession cession = cessionRepository.findById(cessionId)
                .orElseThrow(() -> new EntityNotFoundException("Cession not found with ID: " + cessionId));

        // Validate file type
        validateFileType(file);

        // Upload to storage using the default bucket
        String storagePath = storageService.uploadFile(file, "cessions/" + cessionId + "/contract", storageService.getDefaultBucketName());

        // Create document entity
        Document document = new Document();
        document.setCession(cession);
        document.setDocumentType("CESSION_CONTRACT"); // Always contract for cession
        document.setFileName(file.getOriginalFilename());
        document.setStoragePath(storagePath);
        document.setMimeType(file.getContentType());

        // Save document
        Document savedDocument = documentRepository.save(document);
        return convertToDto(savedDocument);
    }

    @Transactional
    public boolean deleteDocument(UUID id) {
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            try {
                // Determine which bucket the document is in for deletion
                String targetBucket = storageService.getDefaultBucketName();
                if ("NATIONAL_ID".equals(document.getDocumentType()) || "JOB_CARD".equals(document.getDocumentType())) {
                    targetBucket = storageService.getDocumentsBucketIdJob();
                }
                storageService.deleteFile(document.getStoragePath(), targetBucket);
                documentRepository.delete(document);
                return true;
            } catch (Exception e) {
                System.err.println("Error deleting file from storage: " + e.getMessage());
                // Optionally, handle partial success/failure (e.g., delete from DB but not storage)
                return false;
            }
        }
        return false;
    }

    private void validateFileType(MultipartFile file) {
        System.out.println("Validating file type...");
        System.out.println("File content type: " + file.getContentType());
        System.out.println("Allowed MIME types: " + allowedMimeTypes);
        
        if (file.isEmpty()) {
            System.err.println("File is empty");
            throw new IllegalArgumentException("Cannot upload empty file");
        }
        if (!allowedMimeTypes.contains(file.getContentType())) {
            System.err.println("Invalid file type: " + file.getContentType());
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType() + ". Only " + String.join(", ", allowedMimeTypes) + " are allowed.");
        }
        System.out.println("File type validation passed");
    }
}
