package com.tuempresa.creditflow.creditflow_api.service;

import java.util.Map;

public interface MLModelService {
    /**
     * Predice el score de riesgo basado en características del documento.
     * @param features mapa de features del documento
     * @return scoreImpact estimado
     */
    int predictScore(Map<String, Object> features);
}
