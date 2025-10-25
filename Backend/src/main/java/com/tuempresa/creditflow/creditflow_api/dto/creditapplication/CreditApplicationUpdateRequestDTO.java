package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import com.tuempresa.creditflow.creditflow_api.enums.CreditPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos necesarios para actualizar la solicitud de crédito.")
public class CreditApplicationUpdateRequestDTO {
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

   /* @Schema(description = "Lista de documentos utilizados para el cálculo de la fórmula de riesgo o validación.")
    private List<DocumentFormulaDTO> documentsFormula;*/
}
