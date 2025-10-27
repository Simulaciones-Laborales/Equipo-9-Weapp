package com.tuempresa.creditflow.creditflow_api.dto.bcra;

import java.util.List;

public record BcraCausalCheque (
        String causal, // "SIN FONDOS" o "Defectos formales" [cite: 271]
        List<BcraEntidadCheque> entidades
) {
}
