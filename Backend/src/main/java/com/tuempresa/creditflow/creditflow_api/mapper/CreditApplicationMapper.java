package com.tuempresa.creditflow.creditflow_api.mapper;

import com.tuempresa.creditflow.creditflow_api.enums.CreditStatus;
import com.tuempresa.creditflow.creditflow_api.model.CreditApplication;
import com.tuempresa.creditflow.creditflow_api.model.Company;
import com.tuempresa.creditflow.creditflow_api.model.RiskDocument;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.RiskDocumentDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CreditApplicationMapper {

    public static CreditApplication toEntity(CreditApplicationRequestDTO dto, Company company) {
        return CreditApplication.builder()
                .company(company)
                .amount(dto.getAmount())
                .status(CreditStatus.PENDING) // por defecto al crear
                .creditPurpose(dto.getCreditPurpose())  // nuevo
                .termMonths(dto.getTermMonths())        // nuevo
                .build();
    }

    public static CreditApplicationResponseDTO toDTO(CreditApplication entity) {
        return CreditApplicationResponseDTO.builder()
                .idCreditApplication(entity.getId())
                .companyId(entity.getCompany().getId())
                .companyName(entity.getCompany().getCompany_name())
                .amount(entity.getAmount())
                .creditPurpose(entity.getCreditPurpose())   // nuevo
                .termMonths(entity.getTermMonths())         // nuevo
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .riskScore(entity.getRiskScore())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .documents(mapDocuments(entity.getRiskDocuments())) // nuevo
                .build();
    }

    private static List<RiskDocumentDTO> mapDocuments(List<RiskDocument> documents) {
        if (documents == null) return List.of();
        return documents.stream()
                .map(doc -> RiskDocumentDTO.builder()
                        .id(doc.getId())
                        .name(doc.getName())
                        .documentUrl(doc.getDocumentUrl())
                        .scoreImpact(doc.getScoreImpact())
                        .build())
                .collect(Collectors.toList());
    }
}
