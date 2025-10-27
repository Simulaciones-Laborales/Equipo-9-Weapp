package com.tuempresa.creditflow.creditflow_api.dto.bcra;


import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

public record BcraSummaryDTO(
        @Schema(description = "Indica si la consulta al BCRA fue exitosa.")
        boolean isConsulted,

        @Schema(description = "True si se encontró situación 3, 4 o 5 (Riesgo Medio/Alto).")
        boolean hasSeriousDebt,

        @Schema(description = "True si tiene cheques rechazados con multa IMPAGA.")
        boolean hasUnpaidCheques,

        @Schema(description = "La peor situación crediticia encontrada.")
        String worstSituation,

        @Schema(description = "Lista detallada de las deudas activas.")
        List<BcraEntityDebtDTO> currentDebts
) {}