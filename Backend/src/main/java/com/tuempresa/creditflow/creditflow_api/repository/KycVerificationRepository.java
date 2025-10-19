package com.tuempresa.creditflow.creditflow_api.repository;

import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerificationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import com.tuempresa.creditflow.creditflow_api.model.KycVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KycVerificationRepository extends JpaRepository<KycVerification, UUID> {
    Optional<KycVerification> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    Optional<KycVerification> findByUserIdAndStatus(UUID userId, KycStatus status);

    boolean existsByCompanyId(UUID entityId);

    boolean existsByUserIdAndEntityTypeAndStatus(UUID id, KycEntityType kycEntityType, KycStatus kycStatus);

    boolean existsByCompanyIdAndEntityTypeAndStatus( UUID companyId, KycEntityType kycEntityType, KycStatus kycStatus);
}