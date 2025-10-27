package com.tuempresa.creditflow.creditflow_api.dto.bcra;

public record BcraEntidadDeuda(
        String entidad,
        Integer situacion, // 1 a 5 [cite: 73]
        String fechaSit1,
        Double monto, // En miles de pesos [cite: 93]
        Integer diasAtrasoPago,
        Boolean refinanciaciones,
        Boolean recategorizacionOblig,
        Boolean situacionJuridica,
        Boolean irrecDisposicionTecnica,
        Boolean enRevision, // [cite: 112]
        Boolean procesoJud // [cite: 112]
) {

}
