package com.tuempresa.creditflow.creditflow_api.service.api;

import com.tuempresa.creditflow.creditflow_api.service.MLModelService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MLModelServiceImpl implements MLModelService {
    @Override
    public int predictScore(Map<String, Object> features) {
        // Simulación simple: score según la cantidad de términos financieros y tamaño del documento
        int wordCount = (int) features.getOrDefault("wordCount", 0);
        int financialTerms = (int) features.getOrDefault("financialTermsCount", 0);
        long size = (long) features.getOrDefault("documentSize", 0L);

        int score = financialTerms * 10;       // cada término financiero suma 10
        score += Math.min(wordCount / 50, 20); // cada 50 palabras suma 1 punto hasta 20
        score += (size / 1024 / 1024) < 1 ? 5 : 10; // archivos pequeños <1MB suman 5, >1MB suman 10

        return Math.min(score, 100);           // score máximo 100
    }
}
