package com.tuempresa.creditflow.creditflow_api.service.api.bcra;

// IMPORTACIONES NECESARIAS PARA LA SOLUCIÓN SSL
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy; // Paquete correcto para Apache HC 5.x
import java.security.cert.X509Certificate; // Necesario para la implementación de la interfaz
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
// FIN DE IMPORTACIONES SSL

import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraChequesResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraChequesResults;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraDeudasResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraDeudasResults;

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

    // Inicializa con un RestTemplate que ignora SSL para entornos de desarrollo
    private final RestTemplate restTemplate = createUnsafeRestTemplate();

    private static RestTemplate createUnsafeRestTemplate() {
        try {
            // 1. Crear un contexto SSL que confía en TODOS
            final SSLContext sslContext = SSLContexts.custom()
                    // Reemplazamos TrustAllStrategy.INSTANCE por una implementación lambda o anónima
                    .loadTrustMaterial((X509Certificate[] chain, String authType) -> true) // <-- IMPLEMENTACIÓN CORREGIDA
                    .build();

            // 2. Crear la fábrica de sockets SSL (ignora verificación de Hostname)
            final LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext, (hostname, session) -> true
            );

            // 3. Configurar el gestor de conexiones con la fábrica SSL insegura
            final HttpClient httpClient = HttpClientBuilder.create()
                    // ... (rest of the HttpClient configuration)
                    .setConnectionManager(
                            PoolingHttpClientConnectionManagerBuilder.create()
                                    .setSSLSocketFactory(sslSocketFactory)
                                    .setConnectionFactory(new ManagedHttpClientConnectionFactory())
                                    .build()
                    )
                    .setRedirectStrategy(new DefaultRedirectStrategy())
                    .build();

            // 4. Crear el RequestFactory
            ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

            return new RestTemplate(requestFactory);

        } catch (Exception e) {
            log.error("[BCRA] Error al crear RestTemplate inseguro:", e);
            // Fallback al RestTemplate por defecto
            return new RestTemplate();
        }
    }

    /**
     * Consulta la situación crediticia (deudas) actual del deudor.
     * ... (el resto del método se mantiene igual)
     */
    public Optional<BcraDeudasResults> consultarDeudas(String identificacion) {
        // ... (El cuerpo se mantiene igual)
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
     * ... (el resto del método se mantiene igual)
     */
    public Optional<BcraChequesResults> consultarChequesRechazados(String identificacion) {
        // ... (El cuerpo se mantiene igual)
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