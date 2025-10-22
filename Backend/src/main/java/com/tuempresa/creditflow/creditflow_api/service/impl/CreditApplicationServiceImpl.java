package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.*;
import com.tuempresa.creditflow.creditflow_api.enums.*;
import com.tuempresa.creditflow.creditflow_api.exception.*;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.CompanyNotVerifiedException;
import com.tuempresa.creditflow.creditflow_api.mapper.CreditApplicationMapper;
import com.tuempresa.creditflow.creditflow_api.model.*;
import com.tuempresa.creditflow.creditflow_api.repository.*;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationService;
import com.tuempresa.creditflow.creditflow_api.service.MLModelService;
import com.tuempresa.creditflow.creditflow_api.service.OCRService;
import com.tuempresa.creditflow.creditflow_api.service.api.ImageService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    // -------------------------------------------------
    // Crear solicitud con archivos
    @Override
    @Transactional
    public CreditApplicationResponseDTO createApplicationWithFiles(
            CreditApplicationRequestDTO dto,
            List<MultipartFile> documents,
            User owner) {

        log.info("🚀 Iniciando creación de solicitud de crédito para empresa ID: {}", dto.getCompanyId());

        // Validar empresa y KYC
        Company company = validateCompanyAndKyc(dto.getCompanyId(), owner);
        log.info("✅ Empresa validada: {} ({})", company.getCompany_name(), company.getId());
        validateAmount(dto.getAmount());

        // Crear entidad base con los nuevos atributos
        CreditApplication creditApplication = createCreditApplicationEntity(dto, company);

        // Guardar solicitud inicial
        creditApplication = saveCreditApplication(creditApplication);

        // Subir y asociar documentos
        List<RiskDocument> uploadedDocs = uploadAndAssociateDocuments(creditApplication, documents);

        // Persistir solicitud y calcular puntaje
        creditApplication = persistCreditApplicationWithDocuments(creditApplication);

        //  Registrar historial
        saveCreationHistory(creditApplication, owner);

        //  Mapear a DTO de respuesta
        return CreditApplicationMapper.toDTO(creditApplication);
    }

    // ----------------- Métodos privados -----------------

    private CreditApplication createCreditApplicationEntity(CreditApplicationRequestDTO dto, Company company) {
        CreditApplication creditApplication = CreditApplicationMapper.toEntity(dto, company);
        creditApplication.setOperatorComments(null);
        creditApplication.setRiskScore(0);

        // Asignar nuevos atributos
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
                // 1️⃣ Subir imagen/documento
                String url = imageService.uploadImage(file);

                // 2️⃣ Extraer texto con OCR
                String text = ocrService.extractText(file);

                // 3️⃣ Preparar features para el modelo ML
                Map<String, Object> features = new HashMap<>();
                features.put("wordCount", text.split("\\s+").length);
                features.put("documentSize", file.getSize());
                features.put("financialTermsCount", countFinancialTerms(text)); // método helper que contaría términos financieros relevantes

                // 4️⃣ Calcular score con ML
                int scoreImpact = mlModelService.predictScore(features);

                // 5️⃣ Crear entidad RiskDocument con score calculado
                RiskDocument riskDoc = RiskDocument.builder()
                        .creditApplication(creditApplication)
                        .name(file.getOriginalFilename())
                        .documentUrl(url)
                        .scoreImpact(scoreImpact)
                        .build();

                creditApplication.addRiskDocument(riskDoc);
                uploadedDocs.add(riskDoc);

                log.info("📎 Documento '{}' asociado a la solicitud con score: {}", riskDoc.getName(), scoreImpact);
            } catch (Exception e) {
                log.error("💥 Error procesando archivo '{}': {}", file.getOriginalFilename(), e.getMessage(), e);
                throw new RuntimeException("Error al procesar archivo: " + file.getOriginalFilename(), e);
            }
        }

        // 6️⃣ Recalcular puntaje total de la solicitud
        creditApplication.calculateRiskScore();

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
            creditApplication.calculateRiskScore();
            creditApplication = creditApplicationRepository.saveAndFlush(creditApplication);
            entityManager.refresh(creditApplication);
            log.info("💾 Solicitud guardada con {} documento(s).", creditApplication.getRiskDocuments().size());
            return creditApplication;
        } catch (Exception e) {
            log.error("💥 Error al persistir la solicitud o documentos: {}", e.getMessage(), e);
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
                // 1️⃣ Subir imagen/documento
                String url = imageService.uploadImage(file);

                // 2️⃣ Extraer texto con OCR
                String text = ocrService.extractText(file);

                // 3️⃣ Preparar features para el modelo ML
                Map<String, Object> features = new HashMap<>();
                features.put("wordCount", text.split("\\s+").length);
                features.put("documentSize", file.getSize());
                features.put("financialTermsCount", countFinancialTerms(text));

                // 4️⃣ Calcular score con ML
                int scoreImpact = mlModelService.predictScore(features);

                // 5️⃣ Crear entidad RiskDocument con score calculado
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

        // 6️⃣ Recalcular puntaje total de la solicitud
        creditApplication.calculateRiskScore();

        return uploadedDocs;
    }


    // -------------------------------------------------
    // Obtener solicitud por ID (usuario propietario)
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

    // -------------------------------------------------
    // Listar solicitudes de la empresa del usuario
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

    // -------------------------------------------------
    // Actualizar solicitud (monto, comentarios, documentos)
    @Override
    @Transactional
    public CreditApplicationResponseDTO updateApplication(
            UUID id,
            CreditApplicationUpdateRequestDTO dto,
            List<MultipartFile> newDocuments,
            User owner) {

        log.info("✏️ Actualizando solicitud de crédito ID: {}", id);

        // 1️⃣ Obtener solicitud y validar permisos
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

        // 2️⃣ Actualizar campos básicos
        if (dto.getAmount() != null && dto.getAmount().signum() > 0) {
            app.setAmount(dto.getAmount());
        }
        app.setCreditPurpose(dto.getCreditPurpose());
        app.setTermMonths(dto.getTermMonths());

        if (isOperator && dto.getOperatorComments() != null) {
            app.setOperatorComments(dto.getOperatorComments());
        }

        // 3️⃣ Subir y asociar documentos si hay
        List<RiskDocument> uploadedDocs = new ArrayList<>();
        if (isOwner && newDocuments != null && !newDocuments.isEmpty()) {
            // Limpiar colección actual
            if (app.getRiskDocuments() == null) {
                app.setRiskDocuments(new ArrayList<>());
            } else {
                app.getRiskDocuments().clear();
            }

            // Subir documentos y calcular score por cada uno
            uploadedDocs = uploadAndSaveDocuments(app, newDocuments);
            app.getRiskDocuments().addAll(uploadedDocs);

            saveHistory(app, owner, CreditApplicationActionType.UPDATE,
                    "Documentos actualizados: " + uploadedDocs.size());
        }


        // 4️⃣ Recalcular puntaje de riesgo
        app.calculateRiskScore();

        // 5️⃣ Guardar cambios
        CreditApplication updated = creditApplicationRepository.saveAndFlush(app);

        // 6️⃣ Guardar historial de actualización
        saveHistory(updated, owner, CreditApplicationActionType.UPDATE, "Solicitud actualizada");

        // 7️⃣ Mapear a DTO usando el mapper
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
    public CreditApplicationResponseDTO changeStatus(UUID id, CreditApplicationStatusChangeDTO dto, User user) {
        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        boolean isOwner = app.getCompany() != null && app.getCompany().getUser() != null
                && app.getCompany().getUser().getId().equals(user.getId());
        boolean isOperatorOrAdmin = user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.OPERADOR;

        if (!isOwner && !isOperatorOrAdmin) {
            throw new ConflictException("No tiene permiso para cambiar el estado de esta solicitud");
        }

        CreditStatus newStatus;
        try {
            newStatus = CreditStatus.from(dto.getNewStatus());
            if (newStatus == null) throw new IllegalArgumentException("status nulo");
        } catch (Exception e) {
            throw new ConflictException("Status no válido: " + dto.getNewStatus());
        }

        app.setStatus(newStatus);
        if (dto.getComments() != null && !dto.getComments().isBlank()) {
            app.setOperatorComments(dto.getComments());
        }

        CreditApplication updated = creditApplicationRepository.save(app);

        saveHistory(updated, user, CreditApplicationActionType.STATUS_CHANGE,
                "Estado cambiado a " + newStatus.name());

        return CreditApplicationMapper.toDTO(updated);
    }

    // -------------------------------------------------
    // Eliminar solicitud
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

        saveHistory(app, user, CreditApplicationActionType.DELETION, "Solicitud eliminada");

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

    private void saveHistory(CreditApplication app, User operator, CreditApplicationActionType actionType, String comments) {
        CreditApplicationHistory history = CreditApplicationHistory.builder()
                .creditApplication(app)
                .actionType(actionType)
                .action(actionType.name())
                .comments(comments)
                .operator(operator)
                .createdAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);
    }
}
