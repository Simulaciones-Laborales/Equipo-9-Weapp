package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycBadRequestException;
import com.tuempresa.creditflow.creditflow_api.service.IIdentificationService;
import com.tuempresa.creditflow.creditflow_api.utils.CuilCuitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class IdentificationServiceImpl implements IIdentificationService {
    @Override
    public List<String> getFiscalIds(String originalId, KycEntityType type) {
        if (originalId == null || originalId.isEmpty()) {
            throw new KycBadRequestException("La identificación no puede ser nula o vacía.");
        }

        String cleanedId = originalId.replaceAll("[^0-9]", "");

        if (cleanedId.length() == 11) {
            // Caso 1: Ya es CUIT/CUIL de 11 dígitos (COMPANY o USER conocido). Retornamos la lista con un solo elemento.
            return List.of(cleanedId);
        }

        if (type == KycEntityType.COMPANY) {
            // Caso 2: COMPANY debe tener 11 dígitos y fallar si no los tiene.
            throw new KycBadRequestException("La entidad de tipo COMPANY debe proporcionar CUIT/CDI de 11 dígitos.");
        }

        if (type == KycEntityType.USER) {
            // Caso 3: USER. Generamos todos los CUILs posibles.
            if (cleanedId.length() >= 7 && cleanedId.length() <= 8) {
                try {
                    // Llama al utilitario que devuelve la LISTA de CUILs posibles (20..., 27..., etc.)
                    return CuilCuitUtils.generatePossibleCuilsFromDni(cleanedId);
                } catch (Exception e) {
                    throw new KycBadRequestException("No se pudo generar ningún CUIL válido de 11 dígitos a partir del DNI.");
                }
            }
            throw new KycBadRequestException("El DNI debe tener 7 u 8 dígitos para generar el CUIL de consulta BCRA.");
        }

        throw new KycBadRequestException("Tipo de entidad no soportada para la verificación BCRA.");
    }
}
