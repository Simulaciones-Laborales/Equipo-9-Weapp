package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import com.tuempresa.creditflow.creditflow_api.enums.CreditPurpose;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreditApplicationResponseDTO {

    private UUID idCreditApplication;
    private UUID companyId;
    private String companyName;
    private BigDecimal amount;
    private String status;
    private Integer termMonths;
    private CreditPurpose creditPurpose;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer riskScore;
    private List<RiskDocumentDTO> documents; // 👈 ahora usamos DTOs de documentos
}


