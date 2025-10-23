package com.tuempresa.creditflow.creditflow_api.service.api;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc.ImageUploadException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
@Service
@Slf4j
public class ImageService {
    private final Cloudinary cloudinary;

    public ImageService(@Value("${cloudinary.cloud.name}") String cloudName,
                        @Value("${cloudinary.api.key}") String apiKey,
                        @Value("${cloudinary.api.secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    @SuppressWarnings("unchecked")
    public String uploadFile(MultipartFile file, String subfolder, String publicId) {
        try {
            String resourceType = "auto"; // permite imágenes, PDFs u otros
            String folderPath = "credit-flow/" + subfolder; // raíz + subcarpeta

            if (publicId == null || publicId.isBlank()) {
                publicId = UUID.randomUUID().toString();
            }

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "folder", folderPath,
                    "public_id", publicId,
                    "overwrite", true
            );

            Map<String, Object> result = (Map<String, Object>)
                    cloudinary.uploader().upload(file.getBytes(), uploadParams);

            Object url = Optional.ofNullable(result.get("secure_url")).orElse(result.get("url"));
            return url != null ? url.toString() : null;

        } catch (IOException e) {
            log.error("💥 [Cloudinary] Error subiendo archivo '{}': {}", file.getOriginalFilename(), e.getMessage());
            throw new ImageUploadException("Error al subir archivo: " + e.getMessage());
        }
    }



    public void deleteImage(String fileUrl) {
        try {
            String publicId = extractPublicId(fileUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "auto"));
                log.info("🗑️ Archivo eliminado de Cloudinary: {}", publicId);
            }
        } catch (Exception e) {
            log.error("💥 Error eliminando archivo Cloudinary: {}", e.getMessage());
        }
    }

    // Método auxiliar para extraer el ID público de la URL de la imagen
    private String extractPublicId(String fileUrl) {
        if (fileUrl == null) return null;
        try {
            String[] parts = fileUrl.split("/upload/");
            if (parts.length > 1) {
                return parts[1].replaceAll("\\.[^.]+$", ""); // quita extensión
            }
        } catch (Exception e) {
            log.warn("⚠️ No se pudo extraer publicId de: {}", fileUrl);
        }
        return null;
    }

    public void deleteFolder(String folderPath) {
        try {
            Map result = cloudinary.api().deleteResourcesByPrefix("credit-flow/" + folderPath, ObjectUtils.emptyMap());
            log.info("🗑️ Archivos eliminados de la carpeta '{}': {}", folderPath, result);
        } catch (Exception e) {
            log.error("💥 Error eliminando carpeta '{}': {}", folderPath, e.getMessage(), e);
            throw new ImageUploadException("Error al eliminar carpeta: " + e.getMessage());
        }
    }

}
