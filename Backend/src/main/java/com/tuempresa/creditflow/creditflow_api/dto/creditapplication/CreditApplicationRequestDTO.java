package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditApplicationRequestDTO {

    @NotNull
    private UUID companyId; // identificador de la empresa solicitante

    @NotNull
    @Positive
    private BigDecimal amount;

    // Comentario inicial opcional que quedará en operatorComments
    private String operatorComments;
}

