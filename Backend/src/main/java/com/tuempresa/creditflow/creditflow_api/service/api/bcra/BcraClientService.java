package com.tuempresa.creditflow.creditflow_api.service.api.bcra;

import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraChequesResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraChequesResults;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraDeudasResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraDeudasResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
public class BcraClientService {

    @Value("${bcra.api.url:}")
    private String apiUrl;

    @Value("${bcra.api.deudas.endpoint:}")
    private String deudasEndpoint;

    @Value("${bcra.api.cheques.endpoint:}")
    private String chequesEndpoint;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Consulta la situación crediticia (deudas) actual del deudor.
     * @param identificacion CUIT/CUIL/CDI de 11 dígitos. [cite: 30]
     * @return El resultado de la consulta de deudas.
     */
    public Optional<BcraDeudasResults> consultarDeudas(String identificacion) {
        if (identificacion == null || identificacion.length() != 11) {
            log.error("[BCRA] ID inválida para consulta de deudas: {}", identificacion);
            return Optional.empty();
        }

        String url = apiUrl + deudasEndpoint.replace("{identificacion}", identificacion);

        try {
            ResponseEntity<BcraDeudasResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, BcraDeudasResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().results() != null) {
                log.info("[BCRA] Deudas consultadas para {}: Entidades {}", identificacion,
                        response.getBody().results().periodos().stream().flatMap(p -> p.entidades().stream()).count());
                return Optional.of(response.getBody().results());
            } else if (response.getStatusCodeValue() == 404) {
                // Respuesta 404: No se encontró datos para la identificación ingresada [cite: 132, 134]
                log.info("[BCRA] No se encontraron datos de deudas para ID: {}", identificacion);
                return Optional.empty();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                log.info("[BCRA] No se encontraron datos de deudas (404) para ID: {}", identificacion);
                return Optional.empty();
            }
            log.error("[BCRA] Error HTTP al consultar deudas para {}: {}", identificacion, e.getMessage());
        } catch (Exception e) {
            log.error("[BCRA] Error general al consultar deudas para {}: {}", identificacion, e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Consulta el historial de cheques rechazados para el deudor.
     * @param identificacion CUIT/CUIL/CDI de 11 dígitos. [cite: 239]
     * @return El resultado de la consulta de cheques.
     */
    public Optional<BcraChequesResults> consultarChequesRechazados(String identificacion) {
        if (identificacion == null || identificacion.length() != 11) {
            log.error("[BCRA] ID inválida para consulta de cheques: {}", identificacion);
            return Optional.empty();
        }

        String url = apiUrl + chequesEndpoint.replace("{identificacion}", identificacion);

        try {
            ResponseEntity<BcraChequesResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, BcraChequesResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().results() != null) {
                log.info("[BCRA] Cheques consultados para {}: Causales {}", identificacion, response.getBody().results().causales());
                return Optional.of(response.getBody().results());
            } else if (response.getStatusCodeValue() == 404) {
                // No se encontró datos [cite: 230, 231]
                log.info("[BCRA] No se encontraron cheques rechazados para ID: {}", identificacion);
                return Optional.empty();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                log.info("[BCRA] No se encontraron cheques rechazados (404) para ID: {}", identificacion);
                return Optional.empty();
            }
            log.error("[BCRA] Error HTTP al consultar cheques para {}: {}", identificacion, e.getMessage());
        } catch (Exception e) {
            log.error("[BCRA] Error general al consultar cheques para {}: {}", identificacion, e.getMessage());
        }
        return Optional.empty();
    }
}