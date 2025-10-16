package com.tuempresa.creditflow.creditflow_api.mapper;

import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.model.CreditApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICreditApplicationMapper {

    default CreditApplicationResponseDTO toResponseDto(CreditApplication app) {
        if (app == null) return null;

        return CreditApplicationResponseDTO.builder()
                .idCreditApplication(app.getId())
                .companyId(app.getCompany().getId())
                .companyName(app.getCompany().getCompany_name())
                .amount(app.getAmount())
                .status(app.getStatus().name())
                .operatorComments(app.getOperatorComments())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}