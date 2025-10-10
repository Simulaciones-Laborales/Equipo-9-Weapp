package com.tuempresa.creditflow.creditflow_api.mapper;


import com.tuempresa.creditflow.creditflow_api.model.Company;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyResponseDTO;

public class CompanyMapper {

    public static Company toEntity(CompanyRequestDTO dto, User user) {
        return Company.builder()
                .company_name(dto.getName())
                .taxId(dto.getTaxId())
                .annualIncome(dto.getAnnualIncome())
                .user(user)
                .build();
    }

    public static CompanyResponseDTO toDTO(Company company) {
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

