package com.tuempresa.creditflow.creditflow_api.dto.bcra;

import java.util.List;

public record BcraDeudasResults(String identificacion,
                                String denominacion,
                                List<BcraPeriodoDeuda> periodos) { }
