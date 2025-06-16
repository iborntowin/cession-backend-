package com.example.cessionappbackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import com.example.cessionappbackend.entities.Workplace;

@Data
public class ClientDTO {
    private UUID id;
    private Integer clientNumber;

    @Column(name = "full_name", nullable = false)
    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    private String fullName;

    @Column(unique = true, nullable = false)
    @NotNull(message = "CIN is required")
    private Integer cin; // National ID Card number

    @AssertTrue(message = "CIN must be exactly 8 digits")
    private boolean isCinValid() {
        if (cin == null) return false;
        String cinStr = String.format("%08d", cin);
        return cinStr.length() == 8;
    }

    @Size(max = 255, message = "Phone number cannot exceed 255 characters")
    @Column(length = 255)
    private String phoneNumber;

    // New fields for linked Workplace and Job
    private UUID workplaceId;
    private String workplaceName;

    private UUID jobId;
    private String jobName;

    @Column(length = 255)
    private String address;

    @Column(name = "worker_number", unique = true)
    @NotNull(message = "Worker number is required")
    private Long workerNumber;

    @AssertTrue(message = "Worker number must be exactly 10 digits")
    private boolean isWorkerNumberValid() {
        if (workerNumber == null) return false;
        String workerStr = String.format("%010d", workerNumber);
        return workerStr.length() == 10;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    // Timestamps are usually handled by the system, not sent in create/update requests
    // private OffsetDateTime createdAt;
    // private OffsetDateTime updatedAt;

    // We might add lists of associated cession/document IDs if needed for responses

    public String getFullName() {
        return fullName;
    }

    public Integer getCin() {
        return this.cin;
    }

    public void setCin(Integer cin) {
        this.cin = cin;
    }

    public String getJobName() {
        return jobName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UUID getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(UUID workplaceId) {
        this.workplaceId = workplaceId;
    }

    public String getWorkplaceName() {
        return workplaceName;
    }

    public void setWorkplaceName(String workplaceName) {
        this.workplaceName = workplaceName;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }
}

