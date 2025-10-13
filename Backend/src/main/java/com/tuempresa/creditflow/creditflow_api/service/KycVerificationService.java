package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dto.*;
import com.tuempresa.creditflow.creditflow_api.model.*;
import com.tuempresa.creditflow.creditflow_api.repository.KycVerificationRepository;
import com.tuempresa.creditflow.creditflow_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KycVerificationService {

    private final KycVerificationRepository kycRepo;
    private final UserRepository userRepo;
    private final SumsubService sumsubService;

    public KycVerificationService(KycVerificationRepository kycRepo, UserRepository userRepo, SumsubService sumsubService) {
        this.kycRepo = kycRepo;
        this.userRepo = userRepo;
        this.sumsubService = sumsubService;
    }

    @Transactional
    public KycVerificationResponseDTO startVerification(KycVerificationRequestDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String externalId = sumsubService.createApplicant(user.getId().toString(), user.getEmail());

        KycVerification kyc = KycVerification.builder()
                .user(user)
                .externalReferenceId(externalId)
                .status(KycStatus.PENDING)
                .submissionDate(LocalDateTime.now())
                .build();

        kycRepo.save(kyc);
        return toResponseDTO(kyc);
    }

    public List<KycVerificationResponseDTO> getAll() {
        return kycRepo.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public KycVerificationResponseDTO getById(UUID id) {
        KycVerification kyc = kycRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("KYC not found"));
        return toResponseDTO(kyc);
    }

    @Transactional
    public KycVerificationResponseDTO updateStatus(UUID id, KycStatusUpdateDTO dto) {
        KycVerification kyc = kycRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("KYC not found"));

        kyc.setStatus(dto.getStatus());
        kyc.setVerificationNotes(dto.getNotes());
        kyc.setVerificationDate(LocalDateTime.now());
        kycRepo.save(kyc);

        return toResponseDTO(kyc);
    }

    public void delete(UUID id) {
        kycRepo.deleteById(id);
    }

    private KycVerificationResponseDTO toResponseDTO(KycVerification kyc) {
        KycVerificationResponseDTO dto = new KycVerificationResponseDTO();
        dto.setIdKyc(kyc.getIdKyc());
        dto.setStatus(kyc.getStatus());
        dto.setVerificationNotes(kyc.getVerificationNotes());
        dto.setExternalReferenceId(kyc.getExternalReferenceId());
        dto.setSubmissionDate(kyc.getSubmissionDate());
        dto.setVerificationDate(kyc.getVerificationDate());
        dto.setUserId(kyc.getUser().getId());
        dto.setUserFullName(kyc.getUser().getFirstName() + " " + kyc.getUser().getLastName());
        dto.setUserEmail(kyc.getUser().getEmail());
        return dto;
    }
}
