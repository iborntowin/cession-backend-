package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.ClientDTO;
import com.example.cessionappbackend.entities.Client;
import com.example.cessionappbackend.entities.Job;
import com.example.cessionappbackend.entities.Workplace;
import com.example.cessionappbackend.repositories.ClientRepository;
import com.example.cessionappbackend.repositories.JobRepository;
import com.example.cessionappbackend.repositories.WorkplaceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private JobRepository jobRepository;

    // --- DTO Conversion --- //
    private ClientDTO convertToDto(Client client) {
        if (client == null) return null;
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setClientNumber(client.getClientNumber());
        dto.setFullName(client.getFullName());
        dto.setCin(client.getCin());
        dto.setPhoneNumber(client.getPhoneNumber());
        dto.setAddress(client.getAddress());
        dto.setWorkerNumber(client.getWorkerNumber());

        if (client.getWorkplace() != null) {
            dto.setWorkplaceId(client.getWorkplace().getId());
            dto.setWorkplaceName(client.getWorkplace().getName());
        }
        if (client.getJob() != null) {
            dto.setJobId(client.getJob().getId());
            dto.setJobName(client.getJob().getName());
        }
        return dto;
    }

    private Client convertToEntity(ClientDTO dto) {
        if (dto == null) return null;
        Client client = new Client();
        client.setFullName(dto.getFullName());
        client.setCin(dto.getCin());
        client.setPhoneNumber(dto.getPhoneNumber());
        client.setAddress(dto.getAddress());
        client.setWorkerNumber(dto.getWorkerNumber());

        if (dto.getWorkplaceId() != null) {
            Workplace workplace = workplaceRepository.findById(dto.getWorkplaceId())
                    .orElseThrow(() -> new EntityNotFoundException("Workplace not found with ID: " + dto.getWorkplaceId()));
            client.setWorkplace(workplace);
        }
        if (dto.getJobId() != null) {
            Job job = jobRepository.findById(dto.getJobId())
                    .orElseThrow(() -> new EntityNotFoundException("Job not found with ID: " + dto.getJobId()));
            client.setJob(job);
        }
        return client;
    }

    // --- CRUD Operations --- //

    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClientDTO> searchClients(String name, String workplaceName, String jobName, Integer clientNumber, Integer cin, String phoneNumber, String address, Long workerNumber) {
        Specification<Client> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                String searchTerm = name.toLowerCase();
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")),
                    "%" + searchTerm + "%"
                );
                predicates.add(namePredicate);
            }

            if (StringUtils.hasText(workplaceName)) {
                Join<Client, Workplace> workplaceJoin = root.join("workplace");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(workplaceJoin.get("name")),
                    "%" + workplaceName.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(jobName)) {
                Join<Client, Job> jobJoin = root.join("job");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(jobJoin.get("name")),
                    "%" + jobName.toLowerCase() + "%"
                ));
            }

            if (clientNumber != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("clientNumber"),
                    clientNumber
                ));
            }

            if (cin != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("cin"),
                    cin
                ));
            }

            if (workerNumber != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("workerNumber"),
                    workerNumber
                ));
            }

            if (StringUtils.hasText(phoneNumber)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("phoneNumber")),
                    "%" + phoneNumber.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(address)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("address")),
                    "%" + address.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return clientRepository.findAll(spec).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ClientDTO> getClientById(UUID id) {
        return clientRepository.findById(id).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<ClientDTO> getClientByCin(Integer cin) {
        return clientRepository.findByCin(cin).map(this::convertToDto);
    }

    private Integer generateClientNumber() {
        // Get the highest client number from the database
        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            return 1; // Start with 1 if no clients exist
        }
        
        // Find the highest client number
        Integer maxNumber = clients.stream()
                .map(Client::getClientNumber)
                .filter(number -> number != null)
                .max(Integer::compareTo)
                .orElse(0);
        
        return maxNumber + 1; // Return the next number
    }

    public ClientDTO createClient(ClientDTO clientDto) {
        if (clientRepository.findByCin(clientDto.getCin()).isPresent()) {
            throw new IllegalArgumentException("Client with CIN " + clientDto.getCin() + " already exists.");
        }
        Client client = convertToEntity(clientDto);
        
        // Generate and set the client number
        client.setClientNumber(generateClientNumber());
        
        Client savedClient = clientRepository.save(client);
        return convertToDto(savedClient);
    }

    public Optional<ClientDTO> updateClient(UUID id, ClientDTO clientDto) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + id));

        if (!existingClient.getCin().equals(clientDto.getCin())) {
            if (clientRepository.findByCin(clientDto.getCin()).isPresent()) {
                throw new IllegalArgumentException("Another client with CIN " + clientDto.getCin() + " already exists.");
            }
            existingClient.setCin(clientDto.getCin());
        }

        existingClient.setFullName(clientDto.getFullName());
        existingClient.setPhoneNumber(clientDto.getPhoneNumber());
        existingClient.setAddress(clientDto.getAddress());

        if (clientDto.getWorkplaceId() != null) {
            Workplace newWorkplace = workplaceRepository.findById(clientDto.getWorkplaceId())
                    .orElseThrow(() -> new EntityNotFoundException("Workplace not found with ID: " + clientDto.getWorkplaceId()));
            existingClient.setWorkplace(newWorkplace);
        } else {
            existingClient.setWorkplace(null);
        }

        if (clientDto.getJobId() != null) {
            Job newJob = jobRepository.findById(clientDto.getJobId())
                    .orElseThrow(() -> new EntityNotFoundException("Job not found with ID: " + clientDto.getJobId()));
            existingClient.setJob(newJob);
        } else {
            existingClient.setJob(null);
        }

        Client updatedClient = clientRepository.save(existingClient);
        return Optional.of(convertToDto(updatedClient));
    }

    public boolean deleteClient(UUID id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}