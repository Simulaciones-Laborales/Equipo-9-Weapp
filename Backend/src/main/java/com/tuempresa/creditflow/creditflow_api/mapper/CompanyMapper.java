package com.yourcompany.creditflow.creditflow_api.mapper;

import com.yourcompany.creditflow.creditflow_api.dto.CompanyRequestDTO;
import com.yourcompany.creditflow.creditflow_api.dto.CompanyResponseDTO;
import com.yourcompany.creditflow.creditflow_api.model.Company;
import com.yourcompany.creditflow.creditflow_api.model.User;

public class CompanyMapper {

    public static Company toEntity(CompanyRequestDTO dto, User user) {
        return Company.builder()
                .name(dto.getName())
                .taxId(dto.getTaxId())
                .annualIncome(dto.getAnnualIncome())
                .user(user)
                .build();
    }

    public static CompanyResponseDTO toDTO(Company company) {
        return CompanyResponseDTO.builder()
                .idCompany(company.getIdCompany())
                .name(company.getName())
                .taxId(company.getTaxId())
                .annualIncome(company.getAnnualIncome())
                .createdAt(company.getCreatedAt())
                .userId(company.getUser().getId())
                .userName(company.getUser().getName())
                .build();
    }
}

