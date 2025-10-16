package com.tuempresa.creditflow.creditflow_api.service;

import com.cloudinary.utils.ObjectUtils;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.*;
import com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc.ImageUploadException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycNotFoundException;
import com.tuempresa.creditflow.creditflow_api.exception.userExc.UserNotFoundException;
import com.tuempresa.creditflow.creditflow_api.mapper.KycMapper;
import com.tuempresa.creditflow.creditflow_api.model.*;
import com.tuempresa.creditflow.creditflow_api.repository.KycVerificationRepository;
import com.tuempresa.creditflow.creditflow_api.repository.UserRepository;
import com.tuempresa.creditflow.creditflow_api.service.api.ImageService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KycVerificationService {

    private final KycVerificationRepository kycRepo;
    private final UserRepository userRepo;
    private final SumsubService sumsubService;
    private final ImageService imageService;
    private final KycMapper kycMapper;

    /**
     * Inicia un proceso KYC subiendo archivos a Cloudinary y creando registro local.
     */
    @Transactional
    public KycVerificationResponseDTO startVerificationWithFiles(KycFileUploadRequestDTO dto) {
        UUID userId = dto.userId();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));

        if (kycRepo.existsByUserId(userId)) {
            throw new ValidationException("El usuario ya tiene un proceso KYC registrado.");
        }

        // 1️⃣ Crear applicant en Sumsub (real o mock)
        String externalId = sumsubService.createApplicant(user.getId().toString(), user.getEmail());

        // 2️⃣ Consultar estado actual (mock o real)
        Map<String, Object> applicantStatus = sumsubService.getApplicantStatus(externalId);
        String statusStr = (String) applicantStatus.get("status");
        String notes = (String) applicantStatus.get("verificationNotes");
        String verificationDateStr = (String) applicantStatus.get("verificationDate");

        // 3️⃣ Convertir status String -> Enum
        KycStatus status;
        try {
            status = KycStatus.valueOf(statusStr.toUpperCase());
        } catch (Exception e) {
            status = KycStatus.PENDING;
        }

        // 4️⃣ Crear entidad local
        KycVerification kyc = KycVerification.builder()
                .user(user)
                .externalReferenceId(externalId)
                .status(status)
                .submissionDate(LocalDateTime.now())
                .verificationNotes(notes != null ? notes : "Verificación iniciada.")
                .verificationDate(verificationDateStr != null
                        ? OffsetDateTime.parse(verificationDateStr).toLocalDateTime()
                        : null)
                .build();

        kycRepo.save(kyc);

        // 5️⃣ Subir archivos a Cloudinary
        Map<String, String> uploaded = uploadKycFilesToCloudinary(kyc.getIdKyc(), dto);
        if (!uploaded.isEmpty()) {
            kyc.setSelfieUrl(uploaded.get("selfie"));
            kyc.setDniFrontUrl(uploaded.get("dniFront"));
            kyc.setDniBackUrl(uploaded.get("dniBack"));
            kyc.setVerificationNotes("Archivos cargados correctamente: " + uploaded.keySet());
        }

        kycRepo.save(kyc);
        log.info("KYC iniciado correctamente. kycId={} externalId={} status={}", kyc.getIdKyc(), externalId, status);

        return kycMapper.toResponseDto(kyc);
    }


    /**
     * Sube los archivos de KYC a Cloudinary.
     */
    private Map<String, String> uploadKycFilesToCloudinary(UUID kycId, KycFileUploadRequestDTO dto) {
        Map<String, String> urls = new HashMap<>();

        uploadIfPresent(dto.selfie(), urls, "selfie", kycId);
        uploadIfPresent(dto.dniFront(), urls, "dniFront", kycId);
        uploadIfPresent(dto.dniBack(), urls, "dniBack", kycId);

        return urls;
    }

    private void uploadIfPresent(MultipartFile file, Map<String, String> urls, String field, UUID kycId) {
        if (file != null && !file.isEmpty()) {
            String uploadedUrl = uploadFile(file, "kyc/" + kycId + "/" + field);
            urls.put(field, uploadedUrl);
        }
    }

    /**
     * Sube un archivo individual a Cloudinary y devuelve la URL segura.
     */
    @SuppressWarnings("unchecked")
    private String uploadFile(MultipartFile file, String publicId) {
        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "creditflow/kyc",
                    "public_id", publicId,
                    "overwrite", true
            );

            Map<String, Object> result = (Map<String, Object>)
                    imageService.getCloudinary().uploader().upload(file.getBytes(), uploadParams);

            Object url = Optional.ofNullable(result.get("secure_url")).orElse(result.get("url"));
            return url != null ? url.toString() : null;

        } catch (IOException e) {
            log.error("[KYC] Error subiendo imagen a Cloudinary: {}", e.getMessage());
            throw new ImageUploadException("Error al subir archivo: " + e.getMessage());
        }
    }

    /**
     * Devuelve todas las verificaciones existentes.
     */
    public List<KycVerificationResponseDTO> getAll() {
        return kycRepo.findAll()
                .stream()
                .map(kycMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve un KYC por su ID.
     */
    public KycVerificationResponseDTO getById(UUID id) {
        KycVerification kyc = kycRepo.findById(id)
                .orElseThrow(() -> new KycNotFoundException("KYC no encontrado con ID: " + id));
        return kycMapper.toResponseDto(kyc);
    }

    /**
     * Actualiza el estado de una verificación KYC.
     */
    @Transactional
    public KycVerificationResponseDTO updateStatus(UUID id, KycStatusUpdateDTO dto) {
        KycVerification kyc = kycRepo.findById(id)
                .orElseThrow(() -> new KycNotFoundException("KYC no encontrado con ID: " + id));

        kyc.setStatus(dto.getStatus());
        kyc.setVerificationNotes(
                dto.getNotes() != null ? dto.getNotes() : "Estado actualizado manualmente");
        kyc.setVerificationDate(LocalDateTime.now());

        kycRepo.save(kyc);
        return kycMapper.toResponseDto(kyc);
    }

    /**
     * Elimina un registro de verificación KYC.
     */
    public void delete(UUID id) {
        if (!kycRepo.existsById(id)) {
            throw new KycNotFoundException("No se encontró el KYC con ID: " + id);
        }
        kycRepo.deleteById(id);
    }
}
