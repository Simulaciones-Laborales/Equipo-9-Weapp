package com.tuempresa.creditflow.creditflow_api.dto.bcra;

import java.util.List;

public record BcraChequesResults(
        String identificacion,
        String denominacion,
        List<BcraCausalCheque> causales
) {
}
