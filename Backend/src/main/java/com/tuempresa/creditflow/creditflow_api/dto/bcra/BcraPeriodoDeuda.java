package com.tuempresa.creditflow.creditflow_api.dto.bcra;

import java.util.List;

public record BcraPeriodoDeuda(
        String periodo, // AAAAMM [cite: 72]
        List<BcraEntidadDeuda> entidades
) {
}
