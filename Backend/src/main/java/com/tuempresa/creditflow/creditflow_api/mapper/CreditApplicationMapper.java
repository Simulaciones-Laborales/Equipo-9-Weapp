package com.tuempresa.creditflow.creditflow_api.mapper;


import com.tuempresa.creditflow.creditflow_api.enums.CreditStatus;
import com.tuempresa.creditflow.creditflow_api.model.CreditApplication;
import com.tuempresa.creditflow.creditflow_api.model.Company;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;


public class CreditApplicationMapper {

    public static CreditApplication toEntity(CreditApplicationRequestDTO dto, Company company) {
        return CreditApplication.builder()
                .company(company)
                .amount(dto.getAmount())
                .operatorComments(dto.getOperatorComments())
                .status(CreditStatus.PENDING) //por defecto al crear
                .build();
    }

    public static CreditApplicationResponseDTO toDTO(CreditApplication entity) {
        return CreditApplicationResponseDTO.builder()
                .idCreditApplication(entity.getId())
                .companyId(entity.getCompany().getId())
                .companyName(entity.getCompany().getCompany_name())
                .amount(entity.getAmount())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .operatorComments(entity.getOperatorComments())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

