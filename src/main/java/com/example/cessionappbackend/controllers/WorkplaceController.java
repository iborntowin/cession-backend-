package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.WorkplaceDTO;
import com.example.cessionappbackend.services.WorkplaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workplaces")
public class WorkplaceController {

    @Autowired
    private WorkplaceService workplaceService;

    @GetMapping
    public ResponseEntity<List<WorkplaceDTO>> getAllWorkplaces() {
        return ResponseEntity.ok(workplaceService.getAllWorkplaces());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkplaceDTO> createWorkplace(@Valid @RequestBody WorkplaceDTO workplaceDto) {
        return ResponseEntity.ok(workplaceService.createWorkplace(workplaceDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWorkplace(@PathVariable UUID id) {
        workplaceService.deleteWorkplace(id);
        return ResponseEntity.ok().build();
    }
} 