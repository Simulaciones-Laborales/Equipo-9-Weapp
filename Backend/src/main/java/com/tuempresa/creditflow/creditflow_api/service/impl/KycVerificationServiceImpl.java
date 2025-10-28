package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraChequesResults;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraDeudasResults;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraEntityDebtDTO;
import com.tuempresa.creditflow.creditflow_api.dto.bcra.BcraSummaryDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.*;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycBadRequestException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.KycNotFoundException;
import com.tuempresa.creditflow.creditflow_api.exception.userExc.UserNotFoundException;
import com.tuempresa.creditflow.creditflow_api.mapper.KycMapper;
import com.tuempresa.creditflow.creditflow_api.model.*;
import com.tuempresa.creditflow.creditflow_api.repository.*;
import com.tuempresa.creditflow.creditflow_api.service.IIdentificationService;
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
    private final IIdentificationService identificationService;

    // ====================================================
    //  MÉTODOS PÚBLICOS
    // ====================================================

    @Transactional
    public ExtendedBaseResponse<KycVerificationResponseDTO> startVerificationWithFiles(KycFileUploadRequestDTO dto) {
        validateRequest(dto);
        validateAllDocumentsPresent(dto);

        // 1. Crear la entidad KYC con estado inicial PENDING (sin Sumsub aún)
        KycVerification kyc = createKycEntity(dto.entityType(), dto.entityId());

        // 2. Obtener la lista de identificadores posibles y validar
        List<String> identificaciones = getFiscalIdsToConsult(kyc);
        validateBcraIdentificationList(identificaciones);

        // 3. Consultar BCRA iterando sobre la lista
        BcraSummaryDTO bcraSummary = searchBcraData(kyc, identificaciones);

        // **********************************************
        // 🎯 LÓGICA DE CONTROL DE RIESGO CREDITICIO 🎯
        // **********************************************

        // 3a. Control de Rechazo (Riesgo Grave: Situación 3, 4, 5 o cheques impagos)
        if (bcraSummary.hasSeriousDebt() || bcraSummary.hasUnpaidCheques()) {
            String riskNotes = "Rechazado por riesgo crediticio: "
                    + bcraSummary.worstSituation()
                    + (bcraSummary.hasUnpaidCheques() ? " y Cheques Impagos." : ".");

            kyc.setStatus(KycStatus.REJECTED);
            kyc.setVerificationNotes(riskNotes);
            kyc.setVerificationDate(LocalDateTime.now());
            kycRepo.save(kyc);

            log.error("[KYC RECHAZADO] {} Rechazado por BCRA. Riesgo: {}", kyc.getEntityType(), riskNotes);

            KycVerificationResponseDTO rejectedDto = kycMapper.toResponseDto(kyc, bcraSummary);
            return ExtendedBaseResponse.of(
                    BaseResponse.badRequest("Verificación KYC rechazada por riesgo crediticio."),
                    rejectedDto
            );
        }

        // 3b. Control de Deuda Activa (Cualquier deuda Situación 1/2 con monto > 0)
        boolean hasAnyActiveDebt = bcraSummary.currentDebts().stream()
                .anyMatch(d -> d.situationCode() >= 1 && d.debtAmount() > 0);


        // **********************************************
        // 4. CONTINUAR CON VERIFICACIÓN DE IDENTIDAD (SUMSUB)
        // **********************************************

        // Obtener el ID que fue exitoso en BCRA o el principal para Sumsub
        String fiscalId = identificaciones.stream().findFirst().orElseThrow(() ->
                new IllegalStateException("No se pudo obtener el identificador para Sumsub."));

        // Lógica de Sumsub
        String externalId = createAndLogSumsubApplicant(kyc, fiscalId);
        Map<String, Object> statusInfo = sumsubService.getApplicantStatus(externalId, kyc.getEntityType());

        KycStatus sumsubStatus = parseKycStatus((String) statusInfo.get("status"));

        // 5. DETERMINAR ESTADO FINAL:
        if (hasAnyActiveDebt || sumsubStatus != KycStatus.VERIFIED) {
            // Regla: Si hay deuda activa O Sumsub NO está VERIFIED, pasa a PENDING.
            kyc.setStatus(KycStatus.PENDING);
            String pendingNotes = (hasAnyActiveDebt)
                    ? "Revisión manual requerida: Obligaciones crediticias activas (Situación 1)."
                    : (String) statusInfo.getOrDefault("verificationNotes", "Identidad pendiente de revisión externa.");
            kyc.setVerificationNotes(pendingNotes);

            log.warn("[KYC PENDING] KYC puesto en PENDING por deuda activa o verificación externa.");
        }
        else {
            // Regla: Sin riesgo BCRA (crédito limpio) Y Sumsub VERIFIED.
            kyc.setStatus(KycStatus.VERIFIED);
            kyc.setVerificationNotes("Verificación KYC completada. Crédito limpio e identidad verificada.");
        }

        // Actualizar KYC con datos finales
        kyc.setExternalReferenceId(externalId);

        kycRepo.save(kyc);

        // 6. Carga de Archivos
        Map<String, String> uploadedDocs = uploadKycFiles(kyc.getIdKyc(), dto);
        updateKycWithUploadedDocs(kyc, uploadedDocs);
        kycRepo.save(kyc);

        log.info("[KYC] Verificación final: idKyc={} externalId={} tipo={} estado={}",
                kyc.getIdKyc(), kyc.getExternalReferenceId(), kyc.getEntityType(), kyc.getStatus());

        // 7. Mapear y devolver el DTO
        KycVerificationResponseDTO responseDto = kycMapper.toResponseDto(kyc, bcraSummary);

        return ExtendedBaseResponse.of(
                BaseResponse.created("Verificación KYC iniciada correctamente"),
                responseDto
        );
    }

    // ====================================================
    //  LÓGICA DE BÚSQUEDA Y MAPEO BCRA
    // ====================================================

    /**
     * Itera sobre una lista de IDs hasta encontrar la que devuelve datos del BCRA.
     * @param kyc Entidad KYC
     * @param identificaciones Lista de IDs (ej: [27..., 20...])
     * @return El BcraSummaryDTO del primer resultado exitoso o un resumen vacío/fallido.
     */
    private BcraSummaryDTO searchBcraData(KycVerification kyc, List<String> identificaciones) {

        for (String id : identificaciones) {
            Optional<BcraDeudasResults> deudas = bcraClientService.consultarDeudas(id);
            Optional<BcraChequesResults> cheques = bcraClientService.consultarChequesRechazados(id);

            if (deudas.isPresent() || cheques.isPresent()) {
                log.info("[BCRA ENCONTRADO] Datos obtenidos con ID: {}", id);
                return mapBcraResultsToSummary(kyc, deudas, cheques);
            } else {
                log.warn("[BCRA NO ENCONTRADO] ID: {} no devolvió datos. Probando siguiente...", id);
            }
        }

        log.error("[BCRA FALLA] No se pudo obtener información del BCRA después de probar {} ID(s).", identificaciones.size());

        return new BcraSummaryDTO(
                false,
                false,
                false,
                "Consulta BCRA Fallida o Sin Datos",
                Collections.emptyList()
        );
    }

    /**
     * Procesa los Optionals de resultados del BCRA y construye el resumen final.
     */
    private BcraSummaryDTO mapBcraResultsToSummary(KycVerification kyc, Optional<BcraDeudasResults> deudas, Optional<BcraChequesResults> cheques) {

        boolean isConsulted = deudas.isPresent() || cheques.isPresent();
        int maxDebtSituation = 0;
        List<BcraEntityDebtDTO> debtList = new ArrayList<>();
        boolean hasUnpaidCheques = false;

        // Lógica de Deudas
        if (deudas.isPresent()) {
            List<BcraEntityDebtDTO> mappedDebts = deudas.get().periodos().stream()
                    .flatMap(p -> p.entidades().stream())
                    .map(e -> {
                        return new BcraEntityDebtDTO(
                                e.entidad(),
                                e.situacion(),
                                getSituationDescription(e.situacion()),
                                e.monto(),
                                e.diasAtrasoPago(),
                                e.procesoJud()
                        );
                    })
                    .collect(Collectors.toList());

            debtList.addAll(mappedDebts);

            maxDebtSituation = mappedDebts.stream()
                    .mapToInt(BcraEntityDebtDTO::situationCode)
                    .max()
                    .orElse(0);
        }

        // Lógica de Cheques
        if (cheques.isPresent()) {
            hasUnpaidCheques = cheques.get().causales().stream()
                    .flatMap(c -> c.entidades().stream())
                    .flatMap(e -> e.detalle().stream())
                    .anyMatch(d -> "IMPAGA".equalsIgnoreCase(d.estadoMulta()));
        }

        boolean hasSeriousDebt = maxDebtSituation >= 3;

        if (hasSeriousDebt || hasUnpaidCheques) {
            log.warn("[BCRA] ALERTA: ID={} RIESGO={} CHEQUES={}", kyc.getExternalReferenceId(), maxDebtSituation, hasUnpaidCheques);
        }

        return new BcraSummaryDTO(
                isConsulted,
                hasSeriousDebt,
                hasUnpaidCheques,
                getSituationDescription(maxDebtSituation),
                debtList
        );
    }

    // ====================================================
    //  MÉTODOS DE BÚSQUEDA DE ID y VALIDACIÓN
    // ====================================================

    private String createAndLogSumsubApplicant(KycVerification kyc, String fiscalId) {
        String email;
        KycEntityType type = kyc.getEntityType();
        String entityReference;

        if (type == KycEntityType.USER) {
            email = kyc.getUser().getEmail();
            entityReference = kyc.getUser().getFirstName() + " " + kyc.getUser().getLastName();
        } else if (type == KycEntityType.COMPANY) {
            email = kyc.getCompany().getUser().getEmail();
            entityReference = kyc.getCompany().getCompany_name();
        } else {
            throw new IllegalArgumentException("Tipo de entidad KYC no válido: " + type);
        }

        String externalId = sumsubService.createApplicant(fiscalId, email, type);

        log.info("[Sumsub] Applicant creado para {}: externalId={} fiscalId={}",
                entityReference, externalId, fiscalId);

        return externalId;
    }

    /**
     * Obtiene la lista de identificadores fiscales (CUIT/CUIL) a consultar.
     * @return Lista de identificadores de 11 dígitos.
     */
    private List<String> getFiscalIdsToConsult(KycVerification kyc) {
        String originalId;

        if (kyc.getEntityType() == KycEntityType.USER && kyc.getUser() != null) {
            originalId = kyc.getUser().getDni();
        } else if (kyc.getEntityType() == KycEntityType.COMPANY && kyc.getCompany() != null) {
            originalId = kyc.getCompany().getTaxId();
        } else {
            throw new KycBadRequestException("No se pudo obtener la identificación base para la entidad.");
        }

        return identificationService.getFiscalIds(originalId, kyc.getEntityType());
    }

    /**
     * Valida que la lista de identificadores no sea nula y que cada elemento tenga 11 dígitos.
     */
    private void validateBcraIdentificationList(List<String> identificaciones) {
        if (identificaciones == null || identificaciones.isEmpty()) {
            throw new KycBadRequestException("No se pudo generar identificadores fiscales válidos (CUIT/CUIL).");
        }
        if (identificaciones.stream().anyMatch(id -> !id.matches("^\\d{11}$"))) {
            throw new KycBadRequestException("Error: La identificación fiscal procesada no cumple el formato de 11 dígitos (CUIT/CUIL).");
        }
    }


    // ====================================================
    //  MÉTODOS PÚBLICOS DE CONSULTA (con corrección de mapper)
    // ====================================================

    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<KycVerificationResponseDTO>> getAll() {
        List<KycVerificationResponseDTO> kycs = kycRepo.findAll().stream()
                .map(kyc -> kycMapper.toResponseDto(kyc, null))
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
                .map(kyc -> kycMapper.toResponseDto(kyc, null))
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
                .map(kyc -> kycMapper.toResponseDto(kyc, null))
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
                kycMapper.toResponseDto(kyc, null)
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
                kycMapper.toResponseDto(kyc, null)
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

    @Override
    @Transactional(readOnly = true)
    public ExtendedBaseResponse<List<KycVerificationResponseDTO>> getFiltered(String kycEntityTypeStr, String statusStr) {

        KycEntityType entityTypeEnum = null;
        if (kycEntityTypeStr != null && !kycEntityTypeStr.trim().isEmpty()) {
            try {
                entityTypeEnum = KycEntityType.valueOf(kycEntityTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valor de kycEntityType inválido: " + kycEntityTypeStr);
            }
        }

        KycStatus statusEnum = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                statusEnum = KycStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Valor de status inválido: " + statusStr);
            }
        }

        List<KycVerification> filteredKycs = kycRepo.findFiltered(entityTypeEnum, statusEnum);

        List<KycVerificationResponseDTO> kycs = filteredKycs.stream()
                .map(kyc -> kycMapper.toResponseDto(kyc, null))
                .collect(Collectors.toList());

        return ExtendedBaseResponse.of(
                BaseResponse.ok("Listado de verificaciones KYC filtrado correctamente."),
                kycs
        );
    }

    @Override
    @Transactional(readOnly = true)
// 💡 CAMBIO DE RETORNO: Ahora devuelve KycVerificationResponseDTO, no KycVerifiedCompanyResponseDTO
    public ExtendedBaseResponse<KycVerificationResponseDTO> getCompanyStatusById(UUID companyId) {

        // 1. Buscar la verificación KYC por el ID de la empresa
        KycVerification kyc = kycRepo.findByCompanyId(companyId)
                .orElseThrow(() -> new KycNotFoundException("KYC o empresa no encontrado con ID: " + companyId));

        // 2. Validar que la entidad sea COMPANY (precaución)
        if (kyc.getEntityType() != KycEntityType.COMPANY) {
            throw new KycBadRequestException("La verificación encontrada con ID " + companyId + " no corresponde a una empresa.");
        }

        // 3. Obtener el CUIT/CUIL de la empresa para consultar BCRA
        // Reutilizamos el método getFiscalIdsToConsult (que obtiene CUIT/CUIL de Company)
        List<String> identificaciones = getFiscalIdsToConsult(kyc);
        validateBcraIdentificationList(identificaciones);

        // 4. Consultar BCRA (reutilizando la lógica que itera sobre los IDs)
        BcraSummaryDTO bcraSummary = searchBcraData(kyc, identificaciones);

        // 5. Mapear la entidad actual con el resumen BCRA
        // Nota: El estado del KYC NO se actualiza aquí, solo se lee y se adjunta el nuevo resumen BCRA
        KycVerificationResponseDTO responseDto = kycMapper.toResponseDto(kyc, bcraSummary);

        log.info("[KYC CONSULTA] Estado de KYC para empresa {} consultado. BCRA consultado: {}",
                companyId, bcraSummary.isConsulted());

        // 6. Devolver el DTO en el formato esperado
        return ExtendedBaseResponse.of(
                BaseResponse.ok("Estado KYC de empresa y resumen BCRA obtenidos."),
                responseDto
        );
    }



    // ====================================================
    //  MÉTODOS PRIVADOS AUXILIARES
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

        // CÓDIGO REFRACTORIZADO: No crea Sumsub applicant aquí
        return KycVerification.builder()
                .entityType(KycEntityType.USER)
                .status(KycStatus.PENDING)
                .submissionDate(LocalDateTime.now())
                .verificationNotes("KYC iniciado, pendiente de verificación crediticia y de identidad.")
                .user(user)
                .build();
    }

    private KycVerification createCompanyKyc(UUID companyId) {

        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new KycBadRequestException("Empresa no encontrada con ID: " + companyId));

        if (kycRepo.existsByCompanyId(companyId))
            throw new KycBadRequestException("La empresa ya tiene un proceso KYC.");

        // CÓDIGO REFRACTORIZADO: No crea Sumsub applicant aquí
        return KycVerification.builder()
                .entityType(KycEntityType.COMPANY)
                .status(KycStatus.PENDING)
                .submissionDate(LocalDateTime.now())
                .verificationNotes("KYC iniciado, pendiente de verificación crediticia y de identidad.")
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
    public ExtendedBaseResponse<List<KycVerifiedCompanyResponseDTO>> getVerifiedCompaniesDetails() {

        List<KycVerifiedCompanyResponseDTO> verifiedCompanies =
                kycRepo.findVerifiedCompanyDetails();

        return ExtendedBaseResponse.of(
                BaseResponse.ok("Empresas con verificación"+
                        "KYC obtenidas exitosamente."),
                verifiedCompanies
        );
    }
    /**
     * Mapea la situación numérica a una descripción amigable.
     */
    private String getSituationDescription(int situacion) {
        return switch (situacion) {
            case 1 -> "SITUACION 1: Normal (Riesgo Bajo)";
            case 2 -> "SITUACION 2: Con Seguimiento Especial (Riesgo Moderado)";
            case 3 -> "SITUACION 3: Con Problemas (Riesgo Medio)";
            case 4 -> "SITUACION 4: Con Alto Riesgo de Insolvencia";
            case 5 -> "SITUACION 5: Irrecuperable";
            default -> "Desconocido";
        };
    }
}