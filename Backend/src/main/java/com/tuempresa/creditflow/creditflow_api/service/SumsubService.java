package com.tuempresa.creditflow.creditflow_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SumsubService {

    @Value("${sumsub.api.url:}")
    private String apiUrl;

    @Value("${sumsub.api.token:}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    // 🔹 Crea un "applicant" en modo real o simulado
    public String createApplicant(String externalUserId, String email) {
        // 👉 Si no hay configuración de Sumsub, devolvemos un ID simulado
        if (apiUrl.equals("https://api.sumsub.com") && apiToken.equals("tu_token_real_de_sumsub")) {
            System.out.println("[SumsubService] Modo simulación activo: creando applicant ficticio");
            return "mock-" + UUID.randomUUID();
        }

        // 🔹 Modo real (con credenciales)
        try {
            String endpoint = apiUrl + "/resources/applicants";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("externalUserId", externalUserId);
            requestBody.put("email", email);
            requestBody.put("type", "individual");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);

            if (response.getBody() != null && response.getBody().get("id") != null) {
                return response.getBody().get("id").toString();
            }
            throw new RuntimeException("No se pudo crear el applicant en Sumsub");
        } catch (Exception e) {
            System.err.println("[SumsubService] Error en modo real, usando ID simulado: " + e.getMessage());
            return "mock-" + UUID.randomUUID();
        }
    }

    // 🔹 Consulta de estado (simulada o real)
    public Map<String, Object> getApplicantStatus(String applicantId) {
        if (apiUrl.equals("https://api.sumsub.com") && apiToken.equals("tu_token_real_de_sumsub")) {
            Map<String, Object> mockStatus = new HashMap<>();
            mockStatus.put("applicantId", applicantId);
            mockStatus.put("status", "APPROVED");
            mockStatus.put("comment", "Simulación de aprobación automática");
            return mockStatus;
        }

        try {
            String endpoint = apiUrl + "/resources/applicants/" + applicantId + "/status";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(endpoint, HttpMethod.GET, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("[SumsubService] Error en consulta real, devolviendo estado simulado");
            Map<String, Object> mockStatus = new HashMap<>();
            mockStatus.put("applicantId", applicantId);
            mockStatus.put("status", "PENDING");
            mockStatus.put("comment", "Error al conectar con Sumsub. Estado simulado.");
            return mockStatus;
        }
    }
}
