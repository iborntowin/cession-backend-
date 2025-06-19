package com.example.cessionappbackend.repositories;

import com.example.cessionappbackend.entities.Cession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CessionRepository extends JpaRepository<Cession, UUID> {
    Logger logger = LoggerFactory.getLogger(CessionRepository.class);

    List<Cession> findByClientId(UUID clientId);
    List<Cession> findByStatus(String status);
    List<Cession> findByClientIdAndStatus(UUID clientId, String status);
    List<Cession> findByStartDateBetween(LocalDate start, LocalDate end);

    /**
     * Advanced search query with improved performance and combined criteria support
     */
    @Query(value = "SELECT DISTINCT c.* FROM cessions c " +
           "JOIN clients cl ON cl.id = c.client_id " +
           "LEFT JOIN jobs j ON j.id = cl.job_id " +
           "LEFT JOIN workplaces w ON w.id = cl.workplace_id " +
           "WHERE (:name IS NULL OR LOWER(CAST(cl.full_name AS VARCHAR)) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:job IS NULL OR LOWER(CAST(j.name AS VARCHAR)) LIKE LOWER(CONCAT('%', :job, '%'))) " +
           "AND (:clientNumber IS NULL OR cl.client_number = :clientNumber) " +
           "AND (:clientCin IS NULL OR cl.cin = :clientCin) " +
           "AND (:phoneNumber IS NULL OR LOWER(CAST(cl.phone_number AS VARCHAR)) LIKE LOWER(CONCAT('%', :phoneNumber, '%'))) " +
           "AND (:workplace IS NULL OR LOWER(CAST(w.name AS VARCHAR)) LIKE LOWER(CONCAT('%', :workplace, '%'))) " +
           "AND (:address IS NULL OR LOWER(CAST(cl.address AS VARCHAR)) LIKE LOWER(CONCAT('%', :address, '%'))) " +
           "AND (:workerNumber IS NULL OR cl.worker_number = :workerNumber)", 
           nativeQuery = true)
    List<Cession> searchCessions(
            @Param("name") String name,
            @Param("job") String job,
            @Param("clientNumber") Integer clientNumber,
            @Param("clientCin") Integer clientCin,
            @Param("phoneNumber") String phoneNumber,
            @Param("workplace") String workplace,
            @Param("address") String address,
            @Param("workerNumber") Long workerNumber
    );
}