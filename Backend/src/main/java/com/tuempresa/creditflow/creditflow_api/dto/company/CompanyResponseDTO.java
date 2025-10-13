package com.tuempresa.creditflow.creditflow_api.dto.company;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponseDTO {

    private UUID idCompany;
    private String name;
    private String taxId;
    private BigDecimal annualIncome;
    private LocalDateTime createdAt;
    private UUID userId;
    private String userName;
}

