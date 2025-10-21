package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import com.tuempresa.creditflow.creditflow_api.enums.CreditPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO para la solicitud de creación de una nueva solicitud de crédito.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos necesarios para crear una nueva solicitud de crédito.")
public class CreditApplicationRequestDTO {

    @Schema(
        description = "Identificador único (UUID) de la empresa para la que se solicita el crédito.",
        example = "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        required = true,
        format = "uuid"
    )
    @NotNull
    @Schema(description = "Identificador único de la empresa solicitante.", example = "b91e6a2f-8423-4c9a-92ab-4fae54b122f3")
    private UUID companyId;

    @Schema(
        description = "Monto del crédito solicitado. Debe ser un valor positivo.",
        example = "25000.00",
        required = true,
        minimum = "0.01" // Implica positividad
    )
    @NotNull
    @Positive
    @Schema(description = "Monto solicitado para el crédito.", example = "50000.00")
    private BigDecimal amount;

    @NotNull
    @Schema(description = "Destino o propósito del crédito.", example = "CAPITAL_TRABAJO")
    private CreditPurpose creditPurpose;

    @NotNull
    @Positive
    @Schema(description = "Plazo del crédito en meses.", example = "12")
    private Integer termMonths;

    @Schema(description = "Lista de documentos utilizados para el cálculo de la fórmula de riesgo o validación.")
    private List<DocumentFormulaDTO> documentsFormula;
}
