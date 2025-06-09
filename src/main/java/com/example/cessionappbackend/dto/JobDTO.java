package com.example.cessionappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class JobDTO {
    private UUID id;

    @NotBlank(message = "Job name is required")
    @Size(min = 2, max = 100, message = "Job name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Workplace ID is required")
    private UUID workplaceId;

    private String workplaceName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
} 