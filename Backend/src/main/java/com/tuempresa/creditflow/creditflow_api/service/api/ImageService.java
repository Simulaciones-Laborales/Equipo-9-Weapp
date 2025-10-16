package com.tuempresa.creditflow.creditflow_api.service.api;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc.ImageUploadException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Getter
@Service
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
    public String uploadImage(MultipartFile file) {
        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "images-play-attention"
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            return uploadResult.get("secure_url").toString();
        } catch (Exception ex) {
            throw new ImageUploadException("Error subiendo imagen a Cloudinary: " + ex.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        String publicId = extractPublicId(imageUrl);
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new ImageUploadException("Error deleting image from Cloudinary", e.getMessage());
        }
    }

    // Método auxiliar para extraer el ID público de la URL de la imagen
    private String extractPublicId(String imageUrl) {
        // Extraer el public_id eliminando la parte de Cloudinary en la URL
        String[] parts = imageUrl.split("/");
        String lastPart = parts[parts.length - 1];
        String[] fileNameParts = lastPart.split("\\.");
        return "images/" + fileNameParts[0]; // 🔥 Se agrega el prefijo "images/" para eliminar correctamente
    }
}
