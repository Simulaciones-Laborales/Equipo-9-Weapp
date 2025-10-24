package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.cloudinary.utils.ObjectUtils;
import com.tuempresa.creditflow.creditflow_api.dto.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraChequesResults;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraDeudasResults;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.*;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc.ImageUploadException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycBadRequestException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycNotFoundException;
import com.tuempresa.creditflow.creditflow_api.exception.userExc.UserNotFoundException;
import com.tuempresa.creditflow.creditflow_api.mapper.KycMapper;
import com.tuempresa.creditflow.creditflow_api.model.*;
import com.tuempresa.creditflow.creditflow_api.repository.*;
import com.tuempresa.creditflow.creditflow_api.service.IKycVerificationService;
import com.tuempresa.creditflow.creditflow_api.service.api.ImageService;
import com.tuempresa.creditflow.creditflow_api.service.api.SumsubService;
import com.tuempresa.creditflow.creditflow_api.service.api.bcra.BcraClientService;
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
public class KycVerificationServiceImpl implements IKycVerificationService {

    private final KycVerificationRepository kycRepo;
    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;
    private final SumsubService sumsubService;
    private final ImageService imageService;
    private final KycMapper kycMapper;
    private final BcraClientService bcraClientService;

    // ====================================================
    //  MÉTODOS PÚBLICOS
    // ====================================================

    @Transactional
    public ExtendedBaseResponse<KycVerificationResponseDTO> startVerificationWithFiles(KycFileUploadRequestDTO dto) {
        validateRequest(dto);
        validateAllDocumentsPresent(dto);

        // 1. Crear y guardar la entidad KYC
        KycVerification kyc = createKycEntity(dto.entityType(), dto.entityId());

        // 2. Obtener la identificación y validar su existencia (asumo que se obtiene de los modelos User/Company)
        String identificacion = getKycIdentification(kyc); // <-- NUEVO MÉTODO NECESARIO
        validateBcraIdentification(identificacion); // <-- NUEVO MÉTODO NECESARIO

        // 3. Consultar BCRA antes de guardar el KYC (o después de subir archivos, como prefieras)
        processBcraVerification(kyc, identificacion); // <-- NUEVO MÉTODO A IMPLEMENTAR

        kycRepo.save(kyc); // Guardamos la entidad KYC con la información del BCRA (si se añade un campo)

        // 4. Carga de Archivos
        Map<String, String> uploadedDocs = uploadKycFiles(kyc.getIdKyc(), dto);
        updateKycWithUploadedDocs(kyc, uploadedDocs);
        kycRepo.save(kyc);

        log.info("[KYC] Verificación iniciada: idKyc={} externalId={} tipo={} estado={}",
                kyc.getIdKyc(), kyc.getExternalReferenceId(), dto.entityType(), kyc.getStatus());

        return ExtendedBaseResponse.of(
                BaseResponse.created("Verificación KYC iniciada correctamente"),
                kycMapper.toResponseDto(kyc)
        );
    }

    /**
     * Obtiene la identificación (CUIT/CUIL/CDI) de la entidad KYC.
     */
    private String getKycIdentification(KycVerification kyc) {
        if (kyc.getEntityType() == KycEntityType.USER && kyc.getUser() != null) {
            // ASUMIMOS que el modelo User tiene un campo 'identificacion' (CUIT/CUIL/CDI)
            return kyc.getUser().getDni();
        } else if (kyc.getEntityType() == KycEntityType.COMPANY && kyc.getCompany() != null) {
            // ASUMIMOS que el modelo Company tiene un campo 'identificacion' (CUIT/CUIL/CDI)
            return kyc.getCompany().getTaxId();
        }
        throw new KycBadRequestException("No se pudo obtener la identificación (CUIT/CUIL/CDI) para la entidad.");
    }

    /**
     * Valida que la identificación tenga 11 dígitos, como requiere el BCRA. [cite: 30, 139, 239]
     */
    private void validateBcraIdentification(String identificacion) {
        if (identificacion == null /*|| !identificacion.matches("^\\d{11}$")*/) {
            throw new KycBadRequestException("La identificación (CUIT/CUIL/CDI) debe ser de 11 dígitos para la consulta BCRA.");
        }
    }

    /**
     * Procesa la consulta a la Central de Deudores y Cheques Rechazados.
     */
    private void processBcraVerification(KycVerification kyc, String identificacion) {
        // 1. Consultar Deudas
        Optional<BcraDeudasResults> deudas = bcraClientService.consultarDeudas(identificacion);

        deudas.ifPresent(results -> {
            // Corrección para Deudas: usando getPeriodos() y getEntidades()
            // El primer .stream() es necesario si getPeriodos() retorna una List
            boolean tieneDeudasGraves = results.periodos().stream()
                    .flatMap(p -> p.entidades().stream())
                    .anyMatch(e -> e.situacion() >= 3); // Usar getSituacion() si es POJO

            if (tieneDeudasGraves) {
                log.warn("[BCRA] KYC {}/{} tiene deudas en situación de riesgo (>=3).",
                        kyc.getEntityType(), kyc.getExternalReferenceId());
            }
        });

        // 2. Consultar Cheques Rechazados
        Optional<BcraChequesResults> cheques = bcraClientService.consultarChequesRechazados(identificacion);

        cheques.ifPresent(results -> {
            // Corrección para Cheques: usando getCausales(), getEntidades(), getDetalle() y getEstadoMulta()
            // El primer .stream() es necesario si getCausales() retorna una List
            boolean tieneMultaImpaga = results.causales().stream()
                    .flatMap(c -> c.entidades().stream())
                    .flatMap(e -> e.detalle().stream())
                    .anyMatch(d -> "IMPAGA".equalsIgnoreCase(d.estadoMulta())); // Usar getEstadoMulta() si es POJO

            if (tieneMultaImpaga) {
                log.warn("[BCRA] KYC {}/{} tiene cheques rechazados con multa IMPAGA.",
                        kyc.getEntityType(), kyc.getExternalReferenceId());
            }
        });
    }

    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<KycVerificationResponseDTO>> getAll() {
        List<KycVerificationResponseDTO> kycs = kycRepo.findAll().stream()
                .map(kycMapper::toResponseDto)
                .collect(Collectors.toList());

        return ExtendedBaseResponse.of(
                BaseResponse.ok("Listado completo de KYC"),
                kycs
        );
    }

    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<KycVerificationResponseDTO>> getAllKcyByUserId(UUID userId) {
        if (!userRepo.existsById(userId))
            throw new UserNotFoundException("Usuario no encontrado con ID: " + userId);

        List<KycVerificationResponseDTO> kycs = kycRepo.findByUserId(userId).stream()
                .map(kycMapper::toResponseDto)
                .toList();

        return ExtendedBaseResponse.of(
                BaseResponse.ok("Listado de KYC para el usuario"),
                kycs
        );
    }

    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<KycVerificationResponseDTO>> getAllByUserIdAndOptionalStatus(UUID userId, KycStatus status) {
        if (!userRepo.existsById(userId))
            throw new UserNotFoundException("Usuario no encontrado con ID: " + userId);

        List<KycVerificationResponseDTO> kycs = (status != null
                ? kycRepo.findByUserIdAndStatus(userId, status)
                : kycRepo.findByUserId(userId))
                .stream()
                .map(kycMapper::toResponseDto)
                .toList();

        return ExtendedBaseResponse.of(
                BaseResponse.ok("Listado de KYC filtrado por estado"),
                kycs
        );
    }

    public ExtendedBaseResponse<KycVerificationResponseDTO> getById(UUID id) {
        KycVerification kyc = kycRepo.findById(id)
                .orElseThrow(() -> new KycNotFoundException("KYC no encontrado con ID: " + id));

        return ExtendedBaseResponse.of(
                BaseResponse.ok("KYC encontrado"),
                kycMapper.toResponseDto(kyc)
        );
    }

    @Transactional
    public ExtendedBaseResponse<KycVerificationResponseDTO> updateStatus(UUID id, KycStatusUpdateDTO dto) {
        KycVerification kyc = kycRepo.findById(id)
                .orElseThrow(() -> new KycNotFoundException("KYC no encontrado con ID: " + id));

        kyc.setStatus(dto.getStatus());
        kyc.setVerificationNotes(Optional.ofNullable(dto.getNotes()).orElse("Estado actualizado manualmente"));
        kyc.setVerificationDate(LocalDateTime.now());
        kycRepo.save(kyc);

        return ExtendedBaseResponse.of(
                BaseResponse.ok("Estado de KYC actualizado correctamente"),
                kycMapper.toResponseDto(kyc)
        );
    }

    @Transactional
    public ExtendedBaseResponse<String> delete(UUID id) {
        if (!kycRepo.existsById(id)) {
            throw new KycNotFoundException("No se encontró el KYC con ID: " + id);
        }

        kycRepo.deleteById(id);

        log.info("[KYC] KYC eliminado correctamente: id={}", id);

        return ExtendedBaseResponse.of(
                BaseResponse.ok("KYC eliminado correctamente"),
                id.toString()
        );
    }


    // ====================================================
    //  MÉTODOS PRIVADOS
    // ====================================================

    private void validateRequest(KycFileUploadRequestDTO dto) {
        if (dto.entityId() == null || dto.entityType() == null)
            throw new KycBadRequestException("Debe indicar el ID y el tipo de entidad (USER o COMPANY).");
    }

    private void validateAllDocumentsPresent(KycFileUploadRequestDTO dto) {
        if (dto.document1Url() == null || dto.document2Url() == null || dto.document3Url() == null ||
                dto.document1Url().isEmpty() || dto.document2Url().isEmpty() || dto.document3Url().isEmpty()) {
            throw new KycBadRequestException("Debe adjuntar los 3 documentos requeridos (document1, document2 y document3).");
        }
    }

    private KycVerification createKycEntity(KycEntityType type, UUID entityId) {
        return switch (type) {
            case USER -> createUserKyc(entityId);
            case COMPANY -> createCompanyKyc(entityId);
            default -> throw new IllegalArgumentException("Tipo de entidad KYC no válido: " + type);
        };
    }


    private KycVerification createUserKyc(UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));

        if (kycRepo.existsByUserId(userId))
            throw new ValidationException("El usuario ya tiene un proceso KYC.");

        String externalId = sumsubService.createApplicant(userId.toString(), user.getEmail(), KycEntityType.USER);
        Map<String, Object> statusInfo = sumsubService.getApplicantStatus(externalId, KycEntityType.USER);

        return buildKycVerification(KycEntityType.USER, externalId, statusInfo)
                .user(user)
                .build();
    }

    private KycVerification createCompanyKyc(UUID companyId) {

        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new KycBadRequestException("Empresa no encontrada con ID: " + companyId));

        if (kycRepo.existsByCompanyId(companyId))
            throw new KycBadRequestException("La empresa ya tiene un proceso KYC.");

        String ownerEmail = company.getUser().getEmail();
        String externalId = sumsubService.createApplicant(companyId.toString(), ownerEmail, KycEntityType.COMPANY);
        Map<String, Object> statusInfo = sumsubService.getApplicantStatus(externalId, KycEntityType.COMPANY);

        return buildKycVerification(KycEntityType.COMPANY, externalId, statusInfo)
                .company(company)
                .build();
    }

    private KycVerification.KycVerificationBuilder buildKycVerification(KycEntityType type, String externalId, Map<String, Object> statusInfo) {
        String statusStr = (String) statusInfo.get("status");
        String notes = (String) statusInfo.getOrDefault("verificationNotes", "Verificación iniciada.");
        String dateStr = (String) statusInfo.get("verificationDate");

        return KycVerification.builder()
                .entityType(type)
                .externalReferenceId(externalId)
                .status(parseKycStatus(statusStr))
                .submissionDate(LocalDateTime.now())
                .verificationNotes(notes)
                .verificationDate(dateStr != null ? OffsetDateTime.parse(dateStr).toLocalDateTime() : null);
    }

    private KycStatus parseKycStatus(String statusStr) {
        try {
            return statusStr != null ? KycStatus.valueOf(statusStr.toUpperCase()) : KycStatus.PENDING;
        } catch (IllegalArgumentException e) {
            return KycStatus.PENDING;
        }
    }

    private void updateKycWithUploadedDocs(KycVerification kyc, Map<String, String> uploaded) {
        kyc.setDocument1Url(uploaded.get("document1"));
        kyc.setDocument2Url(uploaded.get("document2"));
        kyc.setDocument3Url(uploaded.get("document3"));
        kyc.setVerificationNotes("Archivos cargados correctamente: " + uploaded.keySet());
    }

    private Map<String, String> uploadKycFiles(UUID kycId, KycFileUploadRequestDTO dto) {
        Map<String, String> uploadedUrls = new HashMap<>();
        Map<String, MultipartFile> files = Map.of(
                "document1", dto.document1Url(),
                "document2", dto.document2Url(),
                "document3", dto.document3Url()
        );

        files.forEach((key, file) -> {
            if (file != null && !file.isEmpty()) {
                String publicId = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String url = imageService.uploadFile(file, "kyc/" + kycId + "/" + key, publicId);
                uploadedUrls.put(key, url);
            }
        });

        return uploadedUrls;
    }

}
