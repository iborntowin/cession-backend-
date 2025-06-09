package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.JobDTO;
import com.example.cessionappbackend.entities.Job;
import com.example.cessionappbackend.entities.Workplace;
import com.example.cessionappbackend.repositories.JobRepository;
import com.example.cessionappbackend.repositories.WorkplaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    private JobDTO convertToDto(Job job) {
        if (job == null) return null;
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        if (job.getWorkplace() != null) {
            dto.setWorkplaceId(job.getWorkplace().getId());
            dto.setWorkplaceName(job.getWorkplace().getName());
        }
        return dto;
    }

    private Job convertToEntity(JobDTO dto) {
        if (dto == null) return null;
        Job job = new Job();
        job.setName(dto.getName());
        if (dto.getWorkplaceId() != null) {
            Workplace workplace = workplaceRepository.findById(dto.getWorkplaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workplace not found"));
            job.setWorkplace(workplace);
        }
        return job;
    }

    @Transactional(readOnly = true)
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<JobDTO> getJobById(UUID id) {
        return jobRepository.findById(id).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<JobDTO> getJobsByWorkplaceId(UUID workplaceId) {
        return jobRepository.findByWorkplaceId(workplaceId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobDTO createJob(JobDTO jobDto) {
        if (jobRepository.existsByName(jobDto.getName())) {
            throw new IllegalArgumentException("A job with this name already exists");
        }
        Job job = convertToEntity(jobDto);
        Job savedJob = jobRepository.save(job);
        return convertToDto(savedJob);
    }

    public Optional<JobDTO> updateJob(UUID id, JobDTO jobDto) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job not found with ID: " + id));

        if (!existingJob.getName().equals(jobDto.getName())) {
            if (jobRepository.existsByName(jobDto.getName())) {
                throw new IllegalArgumentException("Another job with name " + jobDto.getName() + " already exists.");
            }
            existingJob.setName(jobDto.getName());
        }

        if (jobDto.getWorkplaceId() != null && 
            (existingJob.getWorkplace() == null || !existingJob.getWorkplace().getId().equals(jobDto.getWorkplaceId()))) {
            Workplace newWorkplace = workplaceRepository.findById(jobDto.getWorkplaceId())
                    .orElseThrow(() -> new EntityNotFoundException("Workplace not found with ID: " + jobDto.getWorkplaceId()));
            existingJob.setWorkplace(newWorkplace);
        }

        Job updatedJob = jobRepository.save(existingJob);
        return Optional.of(convertToDto(updatedJob));
    }

    @Transactional
    public void deleteJob(UUID id) {
        if (!jobRepository.existsById(id)) {
            throw new IllegalArgumentException("Job not found");
        }
        jobRepository.deleteById(id);
    }
} 