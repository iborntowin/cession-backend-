package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.WorkplaceDTO;
import com.example.cessionappbackend.entities.Workplace;
import com.example.cessionappbackend.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkplaceService {

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private JobService jobService;

    private WorkplaceDTO convertToDto(Workplace workplace) {
        if (workplace == null) return null;
        WorkplaceDTO dto = new WorkplaceDTO();
        dto.setId(workplace.getId());
        dto.setName(workplace.getName());
        dto.setJobs(jobService.getJobsByWorkplaceId(workplace.getId()));
        return dto;
    }

    private Workplace convertToEntity(WorkplaceDTO dto) {
        if (dto == null) return null;
        Workplace workplace = new Workplace();
        workplace.setName(dto.getName());
        return workplace;
    }

    public List<WorkplaceDTO> getAllWorkplaces() {
        return workplaceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkplaceDTO createWorkplace(WorkplaceDTO workplaceDto) {
        if (workplaceRepository.existsByName(workplaceDto.getName())) {
            throw new IllegalArgumentException("A workplace with this name already exists");
        }
        Workplace workplace = convertToEntity(workplaceDto);
        Workplace savedWorkplace = workplaceRepository.save(workplace);
        return convertToDto(savedWorkplace);
    }

    @Transactional
    public void deleteWorkplace(UUID id) {
        if (!workplaceRepository.existsById(id)) {
            throw new IllegalArgumentException("Workplace not found");
        }
        workplaceRepository.deleteById(id);
    }
} 