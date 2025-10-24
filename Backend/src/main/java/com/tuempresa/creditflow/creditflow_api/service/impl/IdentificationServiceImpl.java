package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycBadRequestException;
import com.tuempresa.creditflow.creditflow_api.service.IIdentificationService;
import com.tuempresa.creditflow.creditflow_api.utils.CuilCuitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IdentificationServiceImpl implements IIdentificationService {
    @Override
    public String getFiscalId(String originalId, KycEntityType type) {
        if (originalId == null || originalId.isEmpty()) {
            throw new KycBadRequestException("La identificación no puede ser nula o vacía.");
        }

        String cleanedId = originalId.replaceAll("[^0-9]", "");

        if (cleanedId.length() == 11) {
            // Si ya tiene 11 dígitos, asumimos que es CUIT/CUIL válido para BCRA
            return cleanedId;
        }

        if (type == KycEntityType.COMPANY) {
            // Las empresas SIEMPRE deben proveer CUIT de 11 dígitos
            throw new KycBadRequestException("La entidad de tipo COMPANY debe proporcionar CUIT de 11 dígitos.");
        }

        if (type == KycEntityType.USER) {
            // Usuario: intentamos generar CUIL a partir del DNI.
            if (cleanedId.length() >= 7 && cleanedId.length() <= 8) {
                try {
                    String cuil = CuilCuitUtils.generateCuilFromDni(cleanedId);
                    log.info("[ID Service] DNI ({}) convertido a CUIL: {}", cleanedId, cuil);
                    return cuil;
                } catch (Exception e) {
                    throw new KycBadRequestException("No se pudo generar un CUIL válido de 11 dígitos a partir del DNI.");
                }
            }
            throw new KycBadRequestException("El DNI debe tener 7 u 8 dígitos para generar el CUIL de consulta BCRA.");
        }

        throw new KycBadRequestException("Tipo de entidad no soportada para la verificación BCRA.");
    }
}
