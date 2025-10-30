package com.tuempresa.creditflow.creditflow_api.dto.bcra;

import io.swagger.v3.oas.annotations.media.Schema;

public record BcraEntityDebtDTO(
        @Schema(description = "Nombre de la entidad (ej: BANCO DE LA NACION ARGENTINA)")
        String entityName,

        @Schema(description = "Código de Situación BCRA (1=Normal, 5=Irrecuperable)")
        Integer situationCode,

        @Schema(description = "Descripción de la peor situación (ej: SITUACION 4: Alto Riesgo)")
        String situationDesc,

        @Schema(description = "Monto de la deuda (en miles de pesos)")
        Double debtAmount,

        @Schema(description = "Días de atraso en el pago (0 indica No Aplicable)")
        Integer daysOverdue,

        @Schema(description = "Indica si la deuda está sometida a proceso judicial")
        Boolean isJudicialProcess
) {}
