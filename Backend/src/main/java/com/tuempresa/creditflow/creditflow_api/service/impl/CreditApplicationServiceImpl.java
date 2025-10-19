package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationStatusChangeDTO;
import com.tuempresa.creditflow.creditflow_api.enums.CreditApplicationActionType;
import com.tuempresa.creditflow.creditflow_api.enums.CreditStatus;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import com.tuempresa.creditflow.creditflow_api.exception.ConflictException;
import com.tuempresa.creditflow.creditflow_api.exception.ResourceNotFoundException;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.CompanyNotVerifiedException;
import com.tuempresa.creditflow.creditflow_api.mapper.CreditApplicationMapper;
import com.tuempresa.creditflow.creditflow_api.model.*;
import com.tuempresa.creditflow.creditflow_api.model.User.Role;
import com.tuempresa.creditflow.creditflow_api.repository.*;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditApplicationServiceImpl implements CreditApplicationService {

    private final CreditApplicationRepository creditApplicationRepository;
    private final CompanyRepository companyRepository;
    private final CreditApplicationHistoryRepository historyRepository;
    private final KycVerificationRepository kycVerificationRepository;
    // ---------------------
    // Crear la solicitud para un usuario propietario
    // ---------------------
    @Override
    @Transactional
    public CreditApplicationResponseDTO createApplication(CreditApplicationRequestDTO dto, User owner) {
        // Basic DTO validation (you can move it to controller/validator)
        if(!kycVerificationRepository.existsByCompanyIdAndEntityTypeAndStatus(dto.getCompanyId(), KycEntityType.COMPANY, KycStatus.VERIFIED)){
            throw new CompanyNotVerifiedException("La Pyme no esta verificada, No puede crear una solicitud de crédito");
        }

        // validación básica de los datos del DTO
        if (dto.getCompanyId() == null) {
            throw new ConflictException("Se requiere el ID de la empresa");
        }
        if (dto.getAmount() == null || dto.getAmount().signum() <= 0) {
            throw new ConflictException("El monto debe ser positivo");
        }
        // verificación de que la empresa existe
        Company company = companyRepository.findById(dto.getCompanyId()).orElseThrow(
                () -> new ResourceNotFoundException("No se encontró la empresa: " + dto.getCompanyId()));

        // chequeado de propiedad
        if (company.getUser() == null || !company.getUser().getId().equals(owner.getId())) {
            throw new ResourceNotFoundException("La empresa no es accesible al usuario");
        }
        // mapeado y guardado
        CreditApplication entity = CreditApplicationMapper.toEntity(dto, company);
        CreditApplication saved = creditApplicationRepository.save(entity);

        // inicialización del historial con la creación de la solcitud
        CreditApplicationHistory initial = CreditApplicationHistory.builder()
                .creditApplication(saved)
                .actionType(CreditApplicationActionType.CREATION)
                .action("CREATED")
                .comments(dto.getOperatorComments())
                .operator(owner)
                .createdAt(LocalDateTime.now())
                .build();

        historyRepository.save(initial);

        return CreditApplicationMapper.toDTO(saved);
    }

    // ---------------------
    // Obtener solicitud por id y usuario propietario
    // ---------------------
    @Override
    @Transactional(readOnly = true)
    public CreditApplicationResponseDTO getApplicationByIdAndUser(UUID id, User user) {
        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        if (app.getCompany() == null || app.getCompany().getUser() == null ||
                !app.getCompany().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("La solicitud no es accesible para el usuario");
        }

        return CreditApplicationMapper.toDTO(app);
    }

    // ---------------------
    // Obtener todas las solicitudes de las empresas propiedad del usuario
    // ---------------------
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

    // ---------------------
    // Actualizar datos de la solicitud (monto, comentarios) por el usuario propietario
    // ---------------------
    @Override
    @Transactional
    public CreditApplicationResponseDTO updateApplication(UUID id, CreditApplicationRequestDTO dto, User owner) {
        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        if (app.getCompany() == null || app.getCompany().getUser() == null ||
                !app.getCompany().getUser().getId().equals(owner.getId())) {
            throw new ResourceNotFoundException("La solicitud no corresponde al usuario");
        }

        
        if (dto.getAmount() != null) {
            app.setAmount(dto.getAmount());
        }
        app.setOperatorComments(dto.getOperatorComments());

        CreditApplication updated = creditApplicationRepository.save(app);

        CreditApplicationHistory history = CreditApplicationHistory.builder()
                .creditApplication(updated)
                .actionType(CreditApplicationActionType.UPDATE)
                .action("UPDATED")
                .comments("Updated amount/comment")
                .operator(owner)
                .build();
        historyRepository.save(history);

        return CreditApplicationMapper.toDTO(updated);
    }

    // ---------------------
    // Cambiar estado de la solicitud por el usuario propietario o un operador/administrador
    // ---------------------
    @Override
    @Transactional
    public CreditApplicationResponseDTO changeStatus(UUID id, CreditApplicationStatusChangeDTO dto, User user) {
        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        boolean isOwner = app.getCompany() != null && app.getCompany().getUser() != null &&
                app.getCompany().getUser().getId().equals(user.getId());

        // Los operadores y administradores pueden cambiar el estado de cualquier solicitud
        boolean isOperatorOrAdmin = user.getRole() != null &&
                (user.getRole() == Role.ADMIN || user.getRole() == Role.OPERADOR);

        if (!isOwner && !isOperatorOrAdmin) {
            throw new ResourceNotFoundException("No tiene permiso para cambiar el estado de esta solicitud");
        }

        // parsear y validar nuevo estado
        CreditStatus newStatus;
        try {
            newStatus = CreditStatus.from(dto.getNewStatus());
            if (newStatus == null)
                throw new IllegalArgumentException("status nulo");
        } catch (Exception e) {
            throw new ConflictException("Status no válido" + dto.getNewStatus());
        }
        // actualizar estado y comentarios del operador si lo están
        app.setStatus(newStatus);
        if (dto.getComments() != null && !dto.getComments().isBlank()) {
            app.setOperatorComments(dto.getComments());
        }
        // guardar
        CreditApplication updated = creditApplicationRepository.save(app);

        // guardar también en el historial
        CreditApplicationHistory history = CreditApplicationHistory.builder()
                .creditApplication(updated)
                .actionType(CreditApplicationActionType.STATUS_CHANGE)
                .action("STATUS_CHANGED_TO_" + newStatus.name())
                .comments(dto.getComments())
                .operator(user)
                .build();
        historyRepository.save(history);

        return CreditApplicationMapper.toDTO(updated);
    }

    // ---------------------
    // Eliminar solicitud por id y usuario propietario
    // ---------------------
    @Override
    @Transactional
    public void deleteApplication(UUID id, User user) {
        CreditApplication app = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud: " + id));

        boolean isOwner = app.getCompany() != null &&
                app.getCompany().getUser() != null &&
                app.getCompany().getUser().getId().equals(user.getId());

        boolean isAdmin = user.getRole() != null && user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ResourceNotFoundException("No tiene permiso para eliminar esta solicitud");
        }

        // guardar registro histórico de la eliminación
        CreditApplicationHistory history = CreditApplicationHistory.builder()
                .creditApplication(app)
                .actionType(CreditApplicationActionType.DELETION)
                .action("DELETED")
                .comments("Solicitud eliminada")
                .operator(user)
                .build();
        historyRepository.save(history);
        
        creditApplicationRepository.delete(app);

    }

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
}
