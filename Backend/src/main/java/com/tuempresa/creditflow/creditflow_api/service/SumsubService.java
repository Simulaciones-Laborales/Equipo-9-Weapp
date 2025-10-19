package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

    @Value("${sumsub.mock:false}") // si es true => modo simulación
    private boolean mockMode;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Crea un applicant real o simulado.
     */
    public String createApplicant(String externalId, String email, KycEntityType entityType) {
        if (mockMode || apiUrl.isBlank() || apiToken.isBlank()) {
            String mockId = entityType.name().toLowerCase() + "-mock-" + UUID.randomUUID();
            log.warn("[SumsubService] Modo simulación activo. MOCK applicant {} ID={}", entityType, mockId);
            return mockId;
        }

        try {
            String endpoint = apiUrl + "/resources/applicants";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("externalUserId", externalId);
            body.put("email", email);
            body.put("type", entityType == KycEntityType.COMPANY ? "company" : "individual");

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
            return entityType.name().toLowerCase() + "-mock-" + UUID.randomUUID();
        }
    }

    /**
     * Consulta el estado del applicant (usuario o empresa).
     */
    public Map<String, Object> getApplicantStatus(String applicantId, KycEntityType entityType) {
        if (mockMode || apiUrl.isBlank() || apiToken.isBlank()) {
            return generateMockApplicantStatus(applicantId, entityType);
        }

        try {
            String endpoint = apiUrl + "/resources/applicants/" + applicantId + "/status";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(endpoint, HttpMethod.GET, request, new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> body = response.getBody();
            if (body == null || !"VERIFIED".equals(body.get("status"))) {
                // Si no se recibe estado VERIFICADO, asumimos pendiente
                body = new HashMap<>();
                body.put("status", "PENDING");
                body.put("verificationNotes", "No se pudo verificar el applicant");
                body.put("submissionDate", OffsetDateTime.now().toString());
                body.put("verificationDate", null);
            }

            return body;

        } catch (Exception e) {
            log.error("[SumsubService] Error consultando estado real: {}", e.getMessage());
            // Aquí podés decidir si lanzar excepción o retornar estado pendiente
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("status", "PENDING");
            fallback.put("verificationNotes", "No se pudo verificar el applicant");
            fallback.put("submissionDate", OffsetDateTime.now().toString());
            fallback.put("verificationDate", null);
            return fallback;
        }
    }


    /**
     * Genera un estado simulado dependiendo del tipo de entidad.
     */
    private Map<String, Object> generateMockApplicantStatus(String applicantId, KycEntityType entityType) {
        Map<String, Object> mock = new LinkedHashMap<>();
        String status = "VERIFIED";
        String verificationNotes;
        String verificationDate = OffsetDateTime.now().toString();

        if (entityType == KycEntityType.COMPANY) {
            verificationNotes = "KYC empresarial completado (modo simulación). Se validó CUIT, contrato social y domicilio fiscal.";
        } else {
            verificationNotes = "KYC personal completado (modo simulación). Se validó DNI y selfie.";
        }

        mock.put("applicantId", applicantId);
        mock.put("entityType", entityType.name());
        mock.put("status", status);
        mock.put("verificationNotes", verificationNotes);
        mock.put("submissionDate", OffsetDateTime.now().toString());
        mock.put("verificationDate", verificationDate);
        mock.putAll(generateMockUrls(entityType));
        return mock;
    }

    /**
     * URLs simuladas según tipo.
     */
    private Map<String, String> generateMockUrls(KycEntityType entityType) {
        Map<String, String> urls = new HashMap<>();
        if (entityType == KycEntityType.USER) {
            urls.put("selfieUrl", "https://mockurl/selfie/" + UUID.randomUUID().toString().substring(0, 8));
            urls.put("dniFrontUrl", "https://mockurl/dni_front/" + UUID.randomUUID().toString().substring(0, 8));
            urls.put("dniBackUrl", "https://mockurl/dni_back/" + UUID.randomUUID().toString().substring(0, 8));
        } else {
            urls.put("cuitCertificateUrl", "https://mockurl/cuit/" + UUID.randomUUID().toString().substring(0, 8));
            urls.put("socialContractUrl", "https://mockurl/contract/" + UUID.randomUUID().toString().substring(0, 8));
            urls.put("fiscalAddressUrl", "https://mockurl/fiscal/" + UUID.randomUUID().toString().substring(0, 8));
        }
        return urls;
    }
}