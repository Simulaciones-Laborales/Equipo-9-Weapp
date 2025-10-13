package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditApplicationResponseDTO {

    private UUID idCreditApplication;
    private UUID companyId;
    private String companyName;
    private BigDecimal amount;
    private String status;
    private String operatorComments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

