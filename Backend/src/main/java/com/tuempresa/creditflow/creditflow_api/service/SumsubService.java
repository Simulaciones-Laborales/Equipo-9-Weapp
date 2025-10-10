package com.tuempresa.creditflow.creditflow_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SumsubService {

    @Value("${sumsub.api.url}")
    private String apiUrl;

    @Value("${sumsub.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createApplicant(String externalUserId, String email) {
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
    }

    public Map getApplicantStatus(String applicantId) {
        String endpoint = apiUrl + "/resources/applicants/" + applicantId + "/status";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(endpoint, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }
}