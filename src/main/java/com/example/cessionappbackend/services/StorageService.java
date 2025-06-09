package com.example.cessionappbackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.FileNotFoundException;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    private final Path basePath;
    private final Path idCardPath;
    private final Path jobCardPath;
    private final Path cessionPath;

    public StorageService() {
        // Initialize paths
        this.basePath = Paths.get(System.getProperty("user.home"), "Desktop", "cession-documents");
        this.idCardPath = basePath.resolve("id-cards");
        this.jobCardPath = basePath.resolve("job-cards");
        this.cessionPath = basePath.resolve("cessions");
        
        // Create directories if they don't exist
        try {
            Files.createDirectories(idCardPath);
            Files.createDirectories(jobCardPath);
            Files.createDirectories(cessionPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directories", e);
        }
    }

    /**
     * Upload a file to local storage with a custom file name
     * @param file The file to upload
     * @param targetBucketName The type of document (e.g., "id-job-documents" or "cession-documents")
     * @param path The path within the storage (e.g., "clients/123/national_id")
     * @return The storage path for the uploaded file
     */
    public String uploadFile(MultipartFile file, String targetBucketName, String path) {
        try {
            System.out.println("=== Local Storage Upload Start ===");
            System.out.println("Target bucket: " + targetBucketName);
            System.out.println("Path: " + path);
            System.out.println("Original Filename: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize() + " bytes");
            System.out.println("Content type: " + file.getContentType());

            // Determine target directory based on bucket name
            Path targetDir;
            if ("id-job-documents".equals(targetBucketName)) {
                if (path.contains("id-cards")) {
                    targetDir = idCardPath;
                } else if (path.contains("job-cards")) {
                    targetDir = jobCardPath;
                } else {
                    throw new IllegalArgumentException("Invalid path for id-job-documents bucket");
                }
            } else if ("cessions".equals(targetBucketName)) {
                targetDir = cessionPath;
            } else {
                throw new IllegalArgumentException("Invalid bucket name: " + targetBucketName);
            }

            // Use the provided path as the file name
            String fileName = path;
            System.out.println("Using provided file name: " + fileName);

            // Create the full file path
            Path fullPath = targetDir.resolve(fileName);
            System.out.println("Using directory: " + targetDir);
            System.out.println("Full file path: " + fullPath);

            // Create parent directories if they don't exist
            System.out.println("Creating parent directories if they don't exist");
            Files.createDirectories(fullPath.getParent());

            // Save the file
            System.out.println("Saving file...");
            Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File saved successfully to: " + fullPath);
            System.out.println("=== Local Storage Upload End ===");

            return path;
        } catch (IOException e) {
            System.err.println("Error uploading file: " + e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    /**
     * Generate a file URL for local storage
     * @param path The storage path of the file
     * @param targetBucketName The type of document (e.g., "id-job-documents" or "cession-documents")
     * @return The file URL
     */
    public String generateDownloadUrl(String path, String targetBucketName) {
        try {
            // Determine target directory based on document type
            Path targetDir;
            if ("id-job-documents".equals(targetBucketName)) {
                if (path.contains("id-cards")) {
                    targetDir = idCardPath;
                } else if (path.contains("job-cards")) {
                    targetDir = jobCardPath;
                } else {
                    // If path doesn't contain folder name, determine based on document type
                    targetDir = path.contains("NATIONAL_ID") ? idCardPath : jobCardPath;
                }
            } else if ("cessions".equals(targetBucketName)) {
                targetDir = cessionPath;
            } else {
                throw new IllegalArgumentException("Invalid bucket name: " + targetBucketName);
            }

            // For local storage, return the absolute path
            Path fullPath = targetDir.resolve(path);
            System.out.println("Generating download URL for path: " + path);
            System.out.println("Full path: " + fullPath);
            
            if (!Files.exists(fullPath)) {
                System.err.println("File does not exist at path: " + fullPath);
                throw new FileNotFoundException("File not found: " + path);
            }
            
            return fullPath.toString();
        } catch (Exception e) {
            System.err.println("Error generating download URL for path " + path + ": " + e.getMessage());
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    /**
     * Delete a file from local storage
     * @param storagePath The storage path of the file to delete
     * @param targetBucketName The type of document
     */
    public void deleteFile(String storagePath, String targetBucketName) {
        try {
            Path targetDir;
            if ("id-job-documents".equals(targetBucketName)) {
                if (storagePath.contains("id-cards")) {
                    targetDir = idCardPath;
                } else if (storagePath.contains("job-cards")) {
                    targetDir = jobCardPath;
                } else {
                    throw new IllegalArgumentException("Invalid document type for id-job-documents bucket");
                }
            } else {
                targetDir = cessionPath;
            }

            Path fullPath = targetDir.resolve(storagePath);
            Files.deleteIfExists(fullPath);
            System.out.println("File deleted: " + fullPath);
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
    }

    public String getDefaultBucketName() {
        return "cession-documents";
    }

    public String getDocumentsBucketIdJob() {
        return "id-job-documents";
    }
}
