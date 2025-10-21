package com.tuempresa.creditflow.creditflow_api.service;

import org.springframework.web.multipart.MultipartFile;

public interface OCRService {
    /**
     * Extrae el texto de un documento PDF o imagen.
     * @param file archivo a procesar
     * @return texto extraído
     */
    String extractText(MultipartFile file);
}
