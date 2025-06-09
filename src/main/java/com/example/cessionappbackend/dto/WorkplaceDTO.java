package com.example.cessionappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public class WorkplaceDTO {
    private UUID id;

    @NotBlank(message = "Workplace name is required")
    @Size(min = 2, max = 100, message = "Workplace name must be between 2 and 100 characters")
    private String name;

    private List<JobDTO> jobs;

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

    public List<JobDTO> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobDTO> jobs) {
        this.jobs = jobs;
    }
} 