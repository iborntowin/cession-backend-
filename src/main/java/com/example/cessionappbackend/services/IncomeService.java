package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.IncomeDTO;
import com.example.cessionappbackend.entities.Income;
import com.example.cessionappbackend.repositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;

    @Autowired
    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Transactional
    public IncomeDTO createIncome(IncomeDTO incomeDTO) {
        Income income = new Income();
        income.setAmount(incomeDTO.getAmount());
        income.setSource(incomeDTO.getSource());
        income.setDate(incomeDTO.getDate());
        income.setUserId(incomeDTO.getUserId());
        income.setDescription(incomeDTO.getDescription());

        Income savedIncome = incomeRepository.save(income);
        return convertToDTO(savedIncome);
    }

    public List<IncomeDTO> getIncomesByMonth(UUID userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return incomeRepository.findByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalIncomeForMonth(UUID userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        Double total = incomeRepository.getTotalIncomeForPeriod(userId, startDate, endDate);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    private IncomeDTO convertToDTO(Income income) {
        IncomeDTO dto = new IncomeDTO();
        dto.setId(income.getId());
        dto.setAmount(income.getAmount());
        dto.setSource(income.getSource());
        dto.setDate(income.getDate());
        dto.setUserId(income.getUserId());
        dto.setDescription(income.getDescription());
        return dto;
    }

    public Page<IncomeDTO> getIncomesByDateRange(UUID userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return incomeRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable)
                .map(this::convertToDTO);
    }

    public Page<IncomeDTO> getAllIncomes(UUID userId, Pageable pageable) {
        return incomeRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    public Map<String, Double> getIncomesBySourceForMonth(UUID userId, int year, int month) {
        List<Object[]> results = incomeRepository.getIncomesBySourceForMonth(userId, year, month);
        return results.stream()
            .collect(Collectors.toMap(
                row -> row[0].toString(),
                row -> ((Number) row[1]).doubleValue()
            ));
    }

    @Transactional
    public void deleteIncome(UUID id) {
        incomeRepository.deleteById(id);
    }

    @Transactional
    public Income updateIncome(UUID id, IncomeDTO incomeDTO) {
        Income income = incomeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Income not found"));
        
        income.setSource(incomeDTO.getSource());
        income.setAmount(incomeDTO.getAmount());
        income.setDate(incomeDTO.getDate());
        income.setDescription(incomeDTO.getDescription());
        
        return incomeRepository.save(income);
    }
} 