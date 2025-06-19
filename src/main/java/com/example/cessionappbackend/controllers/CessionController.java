package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.CessionDTO;
import com.example.cessionappbackend.services.CessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/cessions")
@CrossOrigin(origins = "http://localhost:5173")
public class CessionController {

    private static final Logger logger = LoggerFactory.getLogger(CessionController.class);

    private final CessionService cessionService;

    @Autowired
    public CessionController(CessionService cessionService) {
        this.cessionService = cessionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CessionDTO>> getAllCessions() {
        return ResponseEntity.ok(cessionService.getAllCessions());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CessionDTO>> searchCessions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String job,
            @RequestParam(required = false) Integer clientNumber,
            @RequestParam(required = false) Integer clientCin,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String workplace,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Long workerNumber) {
        try {
            logger.debug("Search request received - name: {}, job: {}, clientNumber: {}, clientCin: {}, phoneNumber: {}, workplace: {}, address: {}, workerNumber: {}", 
                    name, job, clientNumber, clientCin, phoneNumber, workplace, address, workerNumber);
            
            // If all parameters are null, return all cessions
            if (name == null && job == null && clientNumber == null && clientCin == null && 
                phoneNumber == null && workplace == null && address == null && workerNumber == null) {
                return ResponseEntity.ok(cessionService.getAllCessions());
            }
            
            List<CessionDTO> cessions = cessionService.searchCessions(name, job, clientNumber, clientCin, phoneNumber, workplace, address, workerNumber);
            logger.debug("Search completed - Found {} cessions", cessions.size());
            return ResponseEntity.ok(cessions);
        } catch (Exception e) {
            logger.error("Error searching cessions", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error searching cessions", e);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CessionDTO> getCessionById(@PathVariable UUID id) {
        try {
            CessionDTO cession = cessionService.getCessionById(id);
            return ResponseEntity.ok(cession);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CessionDTO>> getCessionsByClientId(@PathVariable UUID clientId) {
        try {
            List<CessionDTO> cessions = cessionService.getCessionsByClientId(clientId);
            return ResponseEntity.ok(cessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CessionDTO> createCession(@Valid @RequestBody CessionDTO cessionDto) {
        try {
            CessionDTO createdCession = cessionService.createCession(cessionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCession);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CessionDTO> updateCession(@PathVariable UUID id, @Valid @RequestBody CessionDTO cessionDto) {
        try {
            return cessionService.updateCession(id, cessionDto)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cession not found"));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCession(@PathVariable UUID id) {
        if (cessionService.deleteCession(id)) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cession not found");
        }
    }

    @PostMapping("/recalculate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> recalculateActiveCessions() {
        try {
            cessionService.recalculateAllActiveCessions();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error during cession recalculation", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during recalculation.");
        }
    }
}