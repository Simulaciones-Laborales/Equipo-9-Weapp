package com.tuempresa.creditflow.creditflow_api.dto.bcra;

public record BcraDetalleCheque(Integer nroCheque,
                                String fechaRechazo,
                                Double monto,
                                String fechaPago,
                                String fechaPagoMulta,
                                String estadoMulta, // IMPAGA, SUSPENDIDO, SUSPENDIDA [cite: 271]
                                Boolean ctaPersonal,
                                String denomJuridica,
                                Boolean enRevision,
                                Boolean procesoJud) {
}
