package com.tuempresa.creditflow.creditflow_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@Slf4j
public class SumsubService {

    @Value("${sumsub.api.url:}")
    private String apiUrl;

    @Value("${sumsub.api.token:}")
    private String apiToken;

    @Value("${sumsub.mock:true}")  // si es true => modo simulación
    private boolean mockMode;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Crea un applicant real o simulado.
     */
    public String createApplicant(String externalUserId, String email) {
        if (mockMode || apiUrl.isBlank() || apiToken.isBlank()) {
            String mockId = "mock-" + UUID.randomUUID();
            log.warn("[SumsubService] Modo simulación activo. MOCK applicant ID={}", mockId);
            return mockId;
        }

        try {
            String endpoint = apiUrl + "/resources/applicants";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("externalUserId", externalUserId);
            body.put("email", email);
            body.put("type", "individual");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object id = response.getBody().get("id");
                if (id != null) {
                    return id.toString();
                }
            }

            log.error("[SumsubService] Respuesta inesperada: {}", response.getBody());
            throw new RuntimeException("Error creando applicant");

        } catch (Exception e) {
            log.error("[SumsubService] Error en API real, usando MOCK: {}", e.getMessage());
            return "mock-" + UUID.randomUUID();
        }
    }

    /**
     * Consulta el estado del applicant.
     */
    public Map getApplicantStatus(String applicantId) {
        if (mockMode || apiUrl.isBlank() || apiToken.isBlank()) {
            return generateMockApplicantStatus(applicantId);
        }

        try {
            String endpoint = apiUrl + "/resources/applicants/" + applicantId + "/status";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(endpoint, HttpMethod.GET, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("[SumsubService] Error consultando estado real, generando mock. {}", e.getMessage());
            return generateMockApplicantStatus(applicantId);
        }
    }

    /**
     * Genera un estado simulado dependiendo del modo.
     */
    private Map<String, Object> generateMockApplicantStatus(String applicantId) {
        Map<String, Object> mock = new LinkedHashMap<>();
        String status;
        String verificationNotes;
        String verificationDate;

        if (mockMode) {
            status = "VERIFIED";
            verificationNotes = "Verificación completada automáticamente (modo simulación)";
            verificationDate = OffsetDateTime.now().toString();
        } else {
            status = "PENDING";
            verificationNotes = "Verificación pendiente (modo real)";
            verificationDate = null;
        }

        mock.put("applicantId", applicantId);
        mock.put("status", status);
        mock.put("verificationNotes", verificationNotes);
        mock.put("submissionDate", OffsetDateTime.now().toString());
        mock.put("verificationDate", verificationDate);
        mock.putAll(generateMockUrls());
        return mock;
    }

    /**
     * URLs de archivos simuladas
     */
    private Map<String, String> generateMockUrls() {
        Map<String, String> urls = new HashMap<>();
        urls.put("selfieUrl", "https://mockurl/selfie/" + UUID.randomUUID().toString().substring(0, 8));
        urls.put("dniFrontUrl", "https://mockurl/dni_front/" + UUID.randomUUID().toString().substring(0, 8));
        urls.put("dniBackUrl", "https://mockurl/dni_back/" + UUID.randomUUID().toString().substring(0, 8));
        return urls;
    }
}
