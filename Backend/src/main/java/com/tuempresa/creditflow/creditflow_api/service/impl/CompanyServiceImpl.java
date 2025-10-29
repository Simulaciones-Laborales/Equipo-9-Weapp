package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.company.CompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.exception.kycExc.UserNotVerifiedException;
import com.tuempresa.creditflow.creditflow_api.model.Company;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.repository.CompanyRepository;
import com.tuempresa.creditflow.creditflow_api.repository.KycVerificationRepository;
import com.tuempresa.creditflow.creditflow_api.service.CompanyService;
import com.tuempresa.creditflow.creditflow_api.exception.ResourceNotFoundException;
import com.tuempresa.creditflow.creditflow_api.service.IKycVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final KycVerificationRepository kycVerificationRepository;
    private final IKycVerificationService kycVerificationService;

    @Override
    @Transactional
    public CompanyResponseDTO createCompany(CompanyRequestDTO dto, User user) {
        if (companyRepository.existsByTaxId(dto.getTaxId())) {
            throw new IllegalArgumentException("A company with this tax ID already exists");
        }

        if(!kycVerificationRepository.existsByUserIdAndEntityTypeAndStatus(user.getId(), KycEntityType.USER, KycStatus.VERIFIED)){
            throw new UserNotVerifiedException("EL usuario no esta verificado, No puede crear una empresa");
        }

        Company company = Company.builder()
                .company_name(dto.getName())
                .taxId(dto.getTaxId())
                .annualIncome(dto.getAnnualIncome())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        Company saved = companyRepository.save(company);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<CompanyResponseDTO> getCompaniesByUser(User user) {
        return companyRepository.findByUser(user)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponseDTO getCompanyByIdAndUser(UUID id, User owner) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa"));

        boolean isOwner = company.getUser() != null && company.getUser().getId().equals(owner.getId());
        if (!isOwner) {
            throw new ResourceNotFoundException("El propietario no coincide");
        }
        return mapToResponseDTO(company);
    }

    @Override
    @Transactional
    public CompanyResponseDTO updateCompany(UUID id, CompanyRequestDTO dto, User owner) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa"));

        boolean isOwner = company.getUser() != null && company.getUser().getId().equals(owner.getId());
        if (!isOwner) {
            throw new ResourceNotFoundException("El propietario no coincide");
        }

        if (!company.getTaxId().equals(dto.getTaxId())
                && companyRepository.existsByTaxId(dto.getTaxId())) {
            throw new IllegalArgumentException("Ya está registrada una empresa con este tax ID");
        }

        company.setCompany_name(dto.getName());
        company.setTaxId(dto.getTaxId());
        company.setAnnualIncome(dto.getAnnualIncome());

        Company updated = companyRepository.save(company);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteCompany(UUID id, User owner) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la empresa"));

        boolean isOwner = company.getUser() != null && company.getUser().getId().equals(owner.getId());
        if (!isOwner) {
            throw new ResourceNotFoundException("El propietario no coincide");
        }
        companyRepository.delete(company);
    }

    @Override
    @Transactional(readOnly = true)
    public KycVerificationResponseDTO getCompanyKycByIdAndUser(UUID companyId, User currentUser) {

        // 1. VALIDACIÓN DE PROPIEDAD Y EXISTENCIA
        // Si la empresa no existe O no pertenece al usuario autenticado, lanza ResourceNotFoundException.
        // Usamos el resultado de esta llamada solo para forzar la excepción si no se encuentra.
        companyRepository.findByIdAndUser(companyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa no encontrada o no pertenece al usuario autenticado con ID: " + companyId));

        // 2. OBTENER ESTADO KYC (DELEGACIÓN)
        // Delegamos la búsqueda de KYC, consulta BCRA y mapeo al servicio especializado.
        ExtendedBaseResponse<KycVerificationResponseDTO> responseWrapper =
                kycVerificationService.getCompanyStatusById(companyId);

        // 3. DESEMPAQUETAR y DEVOLVER
        if (responseWrapper.code() == 200) {
            return responseWrapper.data();
        } else {
            // Manejo de errores
            String errorMessage = responseWrapper.message();
            throw new RuntimeException("Fallo al obtener el estado KYC: " + errorMessage);
        }
    }


    private CompanyResponseDTO mapToResponseDTO(Company company) {
        return CompanyResponseDTO.builder()
                .idCompany(company.getId())
                .name(company.getCompany_name())
                .taxId(company.getTaxId())
                .annualIncome(company.getAnnualIncome())
                .createdAt(company.getCreatedAt())
                .userId(company.getUser().getId())
                .userName(company.getUser().getName())
                .build();
    }
}


