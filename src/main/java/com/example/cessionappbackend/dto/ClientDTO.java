package com.example.cessionappbackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ClientDTO {
    private UUID id;
    private Integer clientNumber;

    @NotBlank(message = "Full name cannot be blank")
    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    private String fullName;

    @NotNull(message = "CIN cannot be null")
    @Min(value = 10000000, message = "CIN must be 8 digits")
    @Max(value = 99999999, message = "CIN must be 8 digits")
    private Integer cin;

    @Size(max = 255, message = "Phone number cannot exceed 255 characters")
    private String phoneNumber;

    // New fields for linked Workplace and Job
    private UUID workplaceId;
    private String workplaceName;

    private UUID jobId;
    private String jobName;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Min(value = 10000000, message = "Worker number must be 8 digits")
    @Max(value = 99999999, message = "Worker number must be 8 digits")
    private Integer workerNumber;

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
        return cin;
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
}

