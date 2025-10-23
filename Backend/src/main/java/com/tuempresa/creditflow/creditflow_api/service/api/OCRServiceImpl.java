package com.tuempresa.creditflow.creditflow_api.service.api;

import com.tuempresa.creditflow.creditflow_api.service.OCRService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OCRServiceImpl implements OCRService {
    @Override
    public String extractText(MultipartFile file) {
        // Simulación: devuelve un texto dummy según el nombre del archivo
        if (file == null || file.isEmpty()) return "";
        return "Documento financiero simulado: " + file.getOriginalFilename() + " con datos de ingresos, egresos y deuda.";
    }
}
