package com.tuempresa.creditflow.creditflow_api.dto.bcra;

import java.util.List;

public record BcraEntidadCheque(
        Integer entidad,
        List<BcraDetalleCheque> detalle
) {
}
