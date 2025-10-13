package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.model.Company;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.repository.CompanyRepository;
import com.tuempresa.creditflow.creditflow_api.service.CompanyService;
import com.tuempresa.creditflow.creditflow_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public CompanyResponseDTO createCompany(CompanyRequestDTO dto, User user) {
        if (companyRepository.existsByTaxId(dto.getTaxId())) {
            throw new IllegalArgumentException("A company with this tax ID already exists");
        }
        Company company = Company.builder()
                .company_name(dto.getName())
                .taxId(dto.getTaxId())
                .annualIncome(dto.getAnnualIncome())
                .user(user)
                .build();

        Company saved = companyRepository.save(company);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<CompanyResponseDTO> getCompaniesByUser(User user) {
        return companyRepository.findByUser(user)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponseDTO getCompanyByIdAndUser(UUID id, User owner) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa"));
        
        boolean isOwner = company.getUser() != null && company.getUser().getId().equals(owner.getId());
        if (!isOwner) {
            throw new ResourceNotFoundException("El propietario no coincide");
        }
        return mapToResponseDTO(company);
    }

    @Override
    @Transactional
    public CompanyResponseDTO updateCompany(UUID id, CompanyRequestDTO dto, User owner) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa"));
        
        boolean isOwner = company.getUser() != null && company.getUser().getId().equals(owner.getId());
        if (!isOwner) {
            throw new ResourceNotFoundException("El propietario no coincide");
        }

        if (!company.getTaxId().equals(dto.getTaxId())
                && companyRepository.existsByTaxId(dto.getTaxId())) {
            throw new IllegalArgumentException("Ya está registrada una empresa con este tax ID");
        }

        company.setCompany_name(dto.getName());
        company.setTaxId(dto.getTaxId());
        company.setAnnualIncome(dto.getAnnualIncome());

        Company updated = companyRepository.save(company);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteCompany(UUID id, User owner) {        
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa"));
        
        boolean isOwner = company.getUser() != null && company.getUser().getId().equals(owner.getId());
        if (!isOwner) {
            throw new ResourceNotFoundException("El propietario no coincide");
        }
        companyRepository.delete(company);
    }

    private CompanyResponseDTO mapToResponseDTO(Company company) {
        return CompanyResponseDTO.builder()
                .idCompany(company.getId())
                .name(company.getCompany_name())
                .taxId(company.getTaxId())
                .annualIncome(company.getAnnualIncome())
                .createdAt(company.getCreatedAt())
                .userId(company.getUser().getId())
                .userName(company.getUser().getName())
                .build();
    }
}


