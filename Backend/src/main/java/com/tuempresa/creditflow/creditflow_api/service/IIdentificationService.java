package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;

public interface IIdentificationService {
    /**
     * Obtiene el identificador fiscal (CUIT/CUIL) de 11 dígitos.
     * Si se recibe un DNI, intenta generar el CUIL.
     * @param originalId La identificación base (DNI, CUIT o CUIL).
     * @param type El tipo de entidad.
     * @return El identificador fiscal de 11 dígitos.
     * @throws KycBadRequestException si la identificación es inválida o no se puede generar.
     */
    String getFiscalId(String originalId, KycEntityType type);
}
