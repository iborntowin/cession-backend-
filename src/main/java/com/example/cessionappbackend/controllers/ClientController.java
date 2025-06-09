package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.ClientDTO;
import com.example.cessionappbackend.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    // GET /api/v1/clients - Get all clients
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        logger.debug("Attempting to get all clients. Current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        List<ClientDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }
    
    // GET /api/v1/clients/search - Search clients by name or job
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClientDTO>> searchClients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String workplaceName,
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) Integer clientNumber,
            @RequestParam(required = false) Integer cin,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer workerNumber) {
        logger.debug("Attempting to search clients. Current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        // If all parameters are null, return all clients
        if (name == null && workplaceName == null && jobName == null && clientNumber == null && cin == null &&
            phoneNumber == null && address == null && workerNumber == null) {
            return ResponseEntity.ok(clientService.getAllClients());
        }
        List<ClientDTO> clients = clientService.searchClients(name, workplaceName, jobName, clientNumber, cin, phoneNumber, address, workerNumber);
        return ResponseEntity.ok(clients);
    }

    // GET /api/v1/clients/{id} - Get client by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable UUID id) {
        logger.debug("Attempting to get client by ID. Current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/v1/clients/cin/{cin} - Get client by CIN
    @GetMapping("/cin/{cin}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDTO> getClientByCin(@PathVariable Integer cin) {
        logger.debug("Attempting to get client by CIN. Current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        return clientService.getClientByCin(cin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/v1/clients - Create a new client
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDto) {
        logger.debug("Attempting to create client. Current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        try {
            ClientDTO createdClient = clientService.createClient(clientDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
        } catch (IllegalArgumentException e) {
            // Handle specific errors like duplicate CIN
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // PUT /api/v1/clients/{id} - Update an existing client
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable UUID id, @Valid @RequestBody ClientDTO clientDto) {
        logger.debug("Attempting to update client. Current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        return clientService.updateClient(id, clientDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/v1/clients/{id} - Delete a client
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        logger.debug("Attempting to delete client. Current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        if (clientService.deleteClient(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
