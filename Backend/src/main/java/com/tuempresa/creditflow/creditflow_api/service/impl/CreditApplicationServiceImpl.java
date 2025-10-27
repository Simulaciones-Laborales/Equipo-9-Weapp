package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.*;
import com.tuempresa.creditflow.creditflow_api.dto.user.UserDto;
import com.tuempresa.creditflow.creditflow_api.enums.*;
import com.tuempresa.creditflow.creditflow_api.exception.*;
import com.tuempresa.creditflow.creditflow_api.exception.cloudinaryExc.RiskDocumentNotFoundException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.CompanyNotVerifiedException;
import com.tuempresa.creditflow.creditflow_api.mapper.CreditApplicationMapper;
import com.tuempresa.creditflow.creditflow_api.model.*;
import com.tuempresa.creditflow.creditflow_api.repository.*;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationService;
import com.tuempresa.creditflow.creditflow_api.service.IUserService;
import com.tuempresa.creditflow.creditflow_api.service.MLModelService;
import com.tuempresa.creditflow.creditflow_api.service.OCRService;
import com.tuempresa.creditflow.creditflow_api.service.api.ImageService;
import com.tuempresa.creditflow.creditflow_api.utils.AuthenticationUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditApplicationServiceImpl implements CreditApplicationService {
    private final CreditApplicationRepository creditApplicationRepository;
    private final CompanyRepository companyRepository;
    private final CreditApplicationHistoryRepository historyRepository;
    private final KycVerificationRepository kycVerificationRepository;
    private final ImageService imageService;
    private final RiskDocumentRepository riskDocumentRepository;
    private final EntityManager entityManager;
    private final MLModelService mlModelService;
    private final OCRService ocrService;
    private final AuthenticationUtils authenticationUtils;
    private final IUserService userService;

    // ------------------------------------------------------------------
    // MÉTODO DE AYUDA DENTRO DEL SERVICIO (REEMPLAZA getAuthenticatedUser/getLoggedInUserId)
    // ------------------------------------------------------------------
    private User getAuthenticatedUser() {
        String principal = authenticationUtils.getLoggedInPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("Usuario no autenticado o principal no encontrado.");
        }
        return userService.findEntityByPrincipal(principal);
    }

    // ------------------------------------------------------------------
    // MÉTODOS DE LA INTERFAZ
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<CreditApplicationResponseDTO> getAllCreditApplications(CreditStatus status, Pageable pageable) {
        Page<CreditApplication> applications;

        if (status != null) {
            applications = creditApplicationRepository.findAllByStatus(status, pageable);
        } else {
            applications = creditApplicationRepository.findAll(pageable);
        }

        return applications.map(CreditApplicationMapper::toDTO);
    }

    @Override
    @Transactional
    public ExtendedBaseResponse<Void> purgeAllImageCloudinary() {
        User loggedInUser = null;
        try {
            loggedInUser = getAuthenticatedUser();
        } catch (UnauthorizedException e) {
            return ExtendedBaseResponse.of(BaseResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()), null);
        }
        if (loggedInUser.getRole() != User.Role.OPERADOR && loggedInUser.getRole() != User.Role.ADMIN) {
            log.warn("❌ Intento de purga de contenidos por usuario no autorizado (Rol: {})", loggedInUser.getRole());
            return ExtendedBaseResponse.of(
                    BaseResponse.error(HttpStatus.FORBIDDEN, "Acceso denegado. Solo OPERADORES o ADMIN pueden realizar esta acción."),
                    null
            );
        }

        try {
            log.info("🗑️ Purga de contenidos iniciada por usuario con ID: {}", loggedInUser.getId());
            imageService.deleteFolder("");
            riskDocumentRepository.deleteAll();
            return ExtendedBaseResponse.of(
                    BaseResponse.ok("Todos los contenidos eliminados exitosamente"),
                    null
            );
        } catch (Exception e) {
            log.error("💥 Error eliminando contenidos: {}", e.getMessage(), e);
            return ExtendedBaseResponse.of(
                    BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar los contenidos"),
                    null
            );
        }
    }

    @Transactional
    @Override
    public ExtendedBaseResponse<Void> deleteRiskDocument(UUID id) {
        RiskDocument riskDocument = riskDocumentRepository.findById(id)
                .orElseThrow(() -> new RiskDocumentNotFoundException("Contenido no encontrado"));

        ExtendedBaseResponse<UserDto> loggedInUser;

        try {
            loggedInUser = userService.findOnlineUser();
        } catch (UnauthorizedException e) {
            // Maneja el caso en que el usuario no está en el contexto de seguridad
            return ExtendedBaseResponse.of(BaseResponse.error(HttpStatus.UNAUTHORIZED, "Usuario no autenticado."), null);
        } catch (Exception e) {
            // Captura UserNotFoundException si el principal existe pero no el usuario en DB
            return ExtendedBaseResponse.of(BaseResponse.error(HttpStatus.UNAUTHORIZED, "Error de autenticación."), null);
        }

        User owner = riskDocument.getCreditApplication().getCompany().getUser();
        boolean isOperator = loggedInUser.data().role() == User.Role.OPERADOR;

        if (!isOperator && !owner.getId().equals(loggedInUser.data().id())) {
            log.warn("❌ Intento de eliminar documento {} por usuario no autorizado (ID: {})", id, loggedInUser.data().role());
            throw new ConflictException("No tienes permisos para eliminar este documento. Solo el propietario o un operador puede hacerlo.");
        }

        String filePath = riskDocument.getDocumentUrl();

        try {
            imageService.deleteImage(filePath);
        } catch (Exception e) {
            log.error("Error al eliminar la imagen externa {}: {}", filePath, e.getMessage());
        }

        riskDocumentRepository.delete(riskDocument);

        return ExtendedBaseResponse.of(BaseResponse.ok("Contenido eliminado exitosamente"), null);
    }

    @Override
    @Transactional
    public CreditApplicationResponseDTO createApplicationWithFiles(
            CreditApplicationRequestDTO dto,
            List<MultipartFile> documents,
            User owner) {

        log.info("Iniciando creación de solicitud de crédito para empresa ID: {}", dto.getCompanyId());

        Company company = validateCompanyAndKyc(dto.getCompanyId(), owner);
        log.info("Empresa validada: {} ({})", company.getCompany_name(), company.getId());
        validateAmount(dto.getAmount());

        CreditApplication creditApplication = createCreditApplicationEntity(dto, company);
        creditApplication = saveCreditApplication(creditApplication);

        uploadAndAssociateDocuments(creditApplication, documents);

        creditApplication = persistCreditApplicationWithDocuments(creditApplication);

        saveCreationHistory(creditApplication, owner);

        return CreditApplicationMapper.toDTO(creditApplication);
    }

    // ----------------- Métodos privados -----------------

    private CreditApplication createCreditApplicationEntity(CreditApplicationRequestDTO dto, Company company) {
        CreditApplication creditApplication = CreditApplicationMapper.toEntity(dto, company);
        creditApplication.setOperatorComments(null);
        creditApplication.setRiskScore(0);

        creditApplication.setCreditPurpose(dto.getCreditPurpose());
        creditApplication.setTermMonths(dto.getTermMonths());

        return creditApplication;
    }

    private CreditApplication saveCreditApplication(CreditApplication creditApplication) {
        creditApplication = creditApplicationRepository.save(creditApplication);
        log.info("🆕 Solicitud creada con ID: {}", creditApplication.getId());
        return creditApplication;
    }

    private List<RiskDocument> uploadAndAssociateDocuments(CreditApplication creditApplication, List<MultipartFile> documents) {
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("Debe adjuntar al menos un documento para crear la solicitud.");
        }

        log.info("📂 Se recibieron {} documento(s) para subir.", documents.size());
        List<RiskDocument> uploadedDocs = new ArrayList<>();

        for (MultipartFile file : documents) {
            if (file == null || file.isEmpty()) {
                log.warn("⚠️ Archivo vacío o nulo detectado, se omite.");
                continue;
            }

            try {
                log.info("➡️ Procesando archivo: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

                log.info("📤 Subiendo archivo a ImageService...");
                // ✅ Corrección: pasar carpeta y publicId
                String folderPath = "credit-applications/" + creditApplication.getId();
                String publicId = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String url = imageService.uploadFile(file, folderPath, publicId);
                log.info("✅ Archivo subido correctamente: {}", url);

                log.info("🔍 Ejecutando OCR...");
                String text = ocrService.extractText(file);
                log.info("✅ OCR completado. Longitud de texto extraído: {}",
                        (text != null ? text.length() : 0));

                Map<String, Object> features = new HashMap<>();
                features.put("wordCount", (text != null) ? text.split("\\s+").length : 0);
                features.put("documentSize", file.getSize());
                features.put("financialTermsCount", countFinancialTerms(text));

                log.info("🧠 Ejecutando modelo ML con features: {}", features);

                int scoreImpact = mlModelService.predictScore(features);
                log.info("✅ ML Score calculado: {}", scoreImpact);

                RiskDocument riskDoc = RiskDocument.builder()
                        .creditApplication(creditApplication)
                        .name(file.getOriginalFilename())
                        .documentUrl(url)
                        .scoreImpact(scoreImpact)
                        .build();

                creditApplication.addRiskDocument(riskDoc);
                uploadedDocs.add(riskDoc);

                log.info("📎 Documento '{}' asociado con éxito.", riskDoc.getName());

            } catch (Exception e) {
                log.error("💥 ERROR procesando archivo '{}': {}",
                        file.getOriginalFilename(), e.getMessage(), e);
                throw new RuntimeException("Error al procesar archivo: " + file.getOriginalFilename(), e);
            }
        }

        try {
            creditApplication.calculateRiskScore();
            log.info("📊 Puntaje recalculado correctamente: {}", creditApplication.getRiskScore());
        } catch (Exception e) {
            log.error("💥 ERROR recalculando puntaje: {}", e.getMessage(), e);
            throw e;
        }

        return uploadedDocs;
    }

    // Ejemplo simple del helper countFinancialTerms
    private int countFinancialTerms(String text) {
        if (text == null || text.isBlank()) return 0;
        String[] financialKeywords = {"ingresos", "egresos", "deuda", "activo", "pasivo", "balance", "flujo"};
        int count = 0;
        String lowerText = text.toLowerCase();
        for (String keyword : financialKeywords) {
            if (lowerText.contains(keyword)) count++;
        }
        return count;
    }

    private CreditApplication persistCreditApplicationWithDocuments(CreditApplication creditApplication) {
        try {
            log.info("💾 Guardando solicitud con {} documentos...",
                    creditApplication.getRiskDocuments() != null ? creditApplication.getRiskDocuments().size() : 0);
            creditApplication.calculateRiskScore();
            CreditApplication saved = creditApplicationRepository.saveAndFlush(creditApplication);
            entityManager.refresh(saved);
            log.info("✅ Solicitud guardada correctamente ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("💥 ERROR al persistir la solicitud: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar la solicitud de crédito.", e);
        }
    }

    private void saveCreationHistory(CreditApplication creditApplication, User owner) {
        saveHistory(creditApplication, owner, CreditApplicationActionType.CREATION,
                "Solicitud creada con " + creditApplication.getRiskDocuments().size() + " documento(s)");
        log.info("🧾 Historial registrado correctamente para solicitud {}.", creditApplication.getId());
    }

    // -------------------------------------------------
// Subir y guardar documentos asociados a la solicitud (para update) con ML/OCR
    private List<RiskDocument> uploadAndSaveDocuments(CreditApplication creditApplication, List<MultipartFile> documents) {
        if (documents == null || documents.isEmpty()) return List.of();

        List<RiskDocument> uploadedDocs = new ArrayList<>();

        for (MultipartFile file : documents) {
            if (file == null || file.isEmpty()) continue;

            try {
                // Subir archivo a Cloudinary usando ImageService
                String folderPath = "credit-flow/credit-applications/" + creditApplication.getId();
                String publicId = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String url = imageService.uploadFile(file, folderPath, publicId);

                // Extraer texto con OCR
                String text = ocrService.extractText(file);

                // Generar features para ML
                Map<String, Object> features = new HashMap<>();
                features.put("wordCount", text != null ? text.split("\\s+").length : 0);
                features.put("documentSize", file.getSize());
                features.put("financialTermsCount", countFinancialTerms(text));

                // Predecir impacto en score
                int scoreImpact = mlModelService.predictScore(features);

                // Crear entidad RiskDocument
                RiskDocument doc = RiskDocument.builder()
                        .creditApplication(creditApplication)
                        .name(file.getOriginalFilename())
                        .documentUrl(url)
                        .scoreImpact(scoreImpact)
                        .build();

                uploadedDocs.add(riskDocumentRepository.save(doc));

                log.info("📎 Documento '{}' asociado a la solicitud con score: {}", doc.getName(), scoreImpact);

            } catch (Exception e) {
                log.error("💥 Error procesando archivo '{}': {}", file.getOriginalFilename(), e.getMessage(), e);
                throw new RuntimeException("Error al procesar archivo: " + file.getOriginalFilename(), e);
            }
        }

        // Recalcular puntaje de riesgo
        creditApplication.calculateRiskScore();

        return uploadedDocs;
    }


    @Override
    @Transactional(readOnly = true)
    public CreditApplicationResponseDTO getApplicationByIdAndUser(UUID id, User user) {
        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        if (app.getCompany() == null || app.getCompany().getUser() == null
                || !app.getCompany().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("La solicitud no es accesible para el usuario");
        }

        return CreditApplicationMapper.toDTO(app);
    }

    @Override
    public CreditApplicationResponseDTO getById(UUID id) {
        final var response = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        // CORREGIDO: Reemplazar getAuthenticatedUser()
        final var loggedUser = getAuthenticatedUser();

        if (loggedUser.getRole() == User.Role.PYME) {
            if (!response.getCompany().getUser().getId().equals(loggedUser.getId())) {
                throw new UnauthorizedException("No tienes permisos para acceder a esta solicitud.");
            }
        }

        return CreditApplicationMapper.toDTO(response);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditApplicationResponseDTO> getApplicationsByCompany(UUID companyId, User user) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la empresa: " + companyId));

        if (company.getUser() == null || !company.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("La empresa no es accesible para el usuario");
        }

        return creditApplicationRepository.findByCompany(company)
                .stream()
                .map(CreditApplicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CreditApplicationResponseDTO updateApplication(
            UUID id,
            CreditApplicationUpdateRequestDTO dto,
            List<MultipartFile> newDocuments,
            User owner) {

        log.info("✏️ Actualizando solicitud de crédito ID: {}", id);

        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        boolean isOwner = app.getCompany() != null && app.getCompany().getUser() != null
                && app.getCompany().getUser().getId().equals(owner.getId());
        boolean isOperator = owner.getRole() == User.Role.OPERADOR;

        if (!isOwner && !isOperator) {
            throw new ConflictException("No tiene permisos para modificar esta solicitud");
        }

        if (app.getStatus() == CreditStatus.APPROVED && !isOperator) {
            throw new ConflictException("La solicitud ya está verificada y no puede ser modificada por el usuario PYME");
        }

        if (dto.getAmount() != null && dto.getAmount().signum() > 0) {
            app.setAmount(dto.getAmount());
        }
        app.setCreditPurpose(dto.getCreditPurpose());
        app.setTermMonths(dto.getTermMonths());

        List<RiskDocument> uploadedDocs = new ArrayList<>();
        if (isOwner && newDocuments != null && !newDocuments.isEmpty()) {

            if (app.getRiskDocuments() == null) {
                app.setRiskDocuments(new ArrayList<>());
            } else {
                app.getRiskDocuments().clear();
            }

            uploadedDocs = uploadAndSaveDocuments(app, newDocuments);
            app.getRiskDocuments().addAll(uploadedDocs);

            saveHistory(app, owner, CreditApplicationActionType.UPDATE,
                    "Documentos actualizados: " + uploadedDocs.size());
        }

        app.calculateRiskScore();

        CreditApplication updated = creditApplicationRepository.saveAndFlush(app);

        saveHistory(updated, owner, CreditApplicationActionType.UPDATE, "Solicitud actualizada");

        CreditApplicationResponseDTO response = CreditApplicationMapper.toDTO(updated)
                .toBuilder()
                .documents(updated.getRiskDocuments().stream()
                        .map(doc -> RiskDocumentDTO.builder()
                                .id(doc.getId())
                                .name(doc.getName())
                                .documentUrl(doc.getDocumentUrl())
                                .scoreImpact(doc.getScoreImpact())
                                .build())
                        .toList())
                .riskScore(updated.getRiskScore())
                .build();

        log.info("✅ Solicitud actualizada correctamente ID: {}", updated.getId());
        return response;
    }


    // -------------------------------------------------
    // Cambiar estado de la solicitud
    @Override
    @Transactional
    public CreditApplicationResponseDTO changeStatus(UUID id, CreditApplicationStatusChangeDTO dto, User currentUser) {

        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud de crédito con ID: " + id));

        CreditStatus oldStatus = app.getStatus();
        CreditStatus newStatus = CreditStatus.from(dto.getNewStatus());

        boolean statusChanged = !oldStatus.equals(newStatus);

        validateStatusTransition(oldStatus, newStatus);

        if (statusChanged) {
            app.setStatus(newStatus);
            saveHistory(app, currentUser, CreditApplicationActionType.STATUS_CHANGE,
                    "Estado cambiado de " + oldStatus.name() + " a " + newStatus.name());
        }

        String comments = dto.getComments();
        if (comments != null && !comments.isBlank()) {
            app.setOperatorComments(comments);
            saveHistory(app, currentUser, CreditApplicationActionType.COMMENT, "Operador comentó: " + comments);
        }

        CreditApplication updatedApp = creditApplicationRepository.save(app);

        return CreditApplicationMapper.toDTO(updatedApp);
    }


    private void validateStatusTransition(CreditStatus oldStatus, CreditStatus newStatus) {

        if ((oldStatus == CreditStatus.APPROVED || oldStatus == CreditStatus.REJECTED) &&
                newStatus != oldStatus && newStatus != CreditStatus.UNDER_REVIEW) {
            throw new ConflictException("No se puede cambiar el estado de una solicitud ya finalizada (" + oldStatus.name() + ").");
        }

        if (oldStatus == CreditStatus.PENDING &&
                (newStatus == CreditStatus.APPROVED)) {
            throw new ConflictException("La solicitud debe pasar por revisión antes de ser aprobada.");
        }
    }

    @Override
    @Transactional
    public void deleteApplication(UUID id, User user) {
        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        boolean isOwner = app.getCompany() != null && app.getCompany().getUser() != null
                && app.getCompany().getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == User.Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ConflictException("No tiene permiso para eliminar esta solicitud");
        }

        saveHistory(app, user, CreditApplicationActionType.DELETION, "Solicitud "+id+" fue eliminada");

        creditApplicationRepository.delete(app);
    }

    // -------------------------------------------------
    // Listar solicitudes por usuario
    @Override
    @Transactional(readOnly = true)
    public List<CreditApplicationResponseDTO> getCreditApplicationsByUser(User user, CreditStatus status) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("El usuario autenticado no puede ser nulo");
        }

        List<CreditApplication> applications = (status != null)
                ? creditApplicationRepository.findAllByCompany_User_IdAndStatus(user.getId(), status)
                : creditApplicationRepository.findAllByCompany_User_Id(user.getId());

        return applications.stream()
                .map(CreditApplicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------
    // Helpers
    private Company validateCompanyAndKyc(UUID companyId, User owner) {
        if (!kycVerificationRepository.existsByCompanyIdAndEntityTypeAndStatus(
                companyId, KycEntityType.COMPANY, KycStatus.VERIFIED)) {
            throw new CompanyNotVerifiedException("La empresa no está verificada: " + companyId);
        }
        return companyRepository.findById(companyId).map(company -> {
            if (company.getUser() == null || !company.getUser().getId().equals(owner.getId())) {
                throw new ResourceNotFoundException("La empresa no es accesible al usuario");
            }
            return company;
        }).orElseThrow(() -> new ResourceNotFoundException("No se encontró la empresa: " + companyId));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ConflictException("El monto debe ser positivo");
        }
    }

    private void saveHistory(CreditApplication app, User operator, CreditApplicationActionType actionType, String action) {
        CreditApplicationHistory history = CreditApplicationHistory.builder()
                .creditApplication(app)
                .actionType(actionType)
                .action(action)
                .operator(operator)
                .createdAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);
    }
}
