package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;

import java.util.List;

public interface IIdentificationService {
    /**
     * Obtiene el identificador fiscal (CUIT/CUIL) de 11 dígitos.
     * Si se recibe un DNI, intenta generar el CUIL.
     * @param originalId La identificación base (DNI, CUIT o CUIL).
     * @param type El tipo de entidad.
     * @return El identificador fiscal de 11 dígitos.
     * @throws KycBadRequestException si la identificación es inválida o no se puede generar.
     */
    List<String> getFiscalIds(String originalId, KycEntityType type);
}
