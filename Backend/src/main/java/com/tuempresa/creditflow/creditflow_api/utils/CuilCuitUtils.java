package com.tuempresa.creditflow.creditflow_api.utils;

import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycBadRequestException;

public class CuilCuitUtils {
    // Factores para el cálculo del dígito verificador (DV)
    private static final int[] FACTORS = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    /**
     * Calcula el dígito verificador (DV) para un CUIT/CUIL dado el prefijo y el DNI.
     * @param incompleteId Los primeros 10 dígitos (prefijo + DNI).
     * @return El dígito verificador (0-9).
     */
    private static int calculateDv(String incompleteId) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(incompleteId.charAt(i));
            sum += digit * FACTORS[i];
        }

        int remainder = sum % 11;
        int dv = 11 - remainder;

        if (dv == 11) {
            dv = 0;
        } else if (dv == 10) {
            // Caso especial: si el resultado es 10, se ajusta el prefijo.
            // Esto se maneja en el método principal generateCuilFromDni.
            return 10;
        }

        return dv;
    }

    /**
     * Genera el CUIL de 11 dígitos a partir de un DNI de 8 dígitos.
     * Intenta los prefijos 20, 23, 24 y 27 hasta encontrar uno válido (o el estándar).
     * * @param dni El DNI de 7 u 8 dígitos.
     * @return El CUIL de 11 dígitos.
     */
    public static String generateCuilFromDni(String dni) {
        // Asegura 8 dígitos rellenando con ceros a la izquierda si es necesario
        String dniPadded = String.format("%08d", Long.parseLong(dni.replaceAll("[^0-9]", "")));

        // Prefijos estándar para personas (asumiendo 20/27 para hombre/mujer o 24/23 para casos ambiguos)
        String[] prefixes = {"20", "27", "24", "23"};

        for (String prefix : prefixes) {
            String incompleteId = prefix + dniPadded;
            int dv = calculateDv(incompleteId);

            if (dv < 10) {
                return incompleteId + dv;
            }

            // Si dv es 10, intenta con el siguiente prefijo, excepto si el prefijo es 23/27
            // donde el dv 9 se usa si el resultado es 10.
            if ((prefix.equals("23") || prefix.equals("27")) && dv == 10) {
                return incompleteId + 9;
            }
        }

        throw new KycBadRequestException("No se pudo generar un CUIL válido de 11 dígitos para el DNI proporcionado.");
    }
}
