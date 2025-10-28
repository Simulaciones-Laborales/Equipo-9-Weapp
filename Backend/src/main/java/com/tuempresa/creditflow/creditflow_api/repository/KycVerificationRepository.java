package com.tuempresa.creditflow.creditflow_api.repository;

import com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerifiedCompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import com.tuempresa.creditflow.creditflow_api.model.KycVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface KycVerificationRepository extends JpaRepository<KycVerification, UUID> {
    
    Optional<KycVerification> findByUserId(UUID userId);
    
    boolean existsByUserId(UUID userId);
    
    Optional<KycVerification> findByUserIdAndStatus(UUID userId, KycStatus status);
    
    boolean existsByCompanyId(UUID entityId);
    
    boolean existsByUserIdAndEntityTypeAndStatus(UUID id, KycEntityType kycEntityType, KycStatus kycStatus);
    
    boolean existsByCompanyIdAndEntityTypeAndStatus( UUID companyId, KycEntityType kycEntityType, KycStatus kycStatus);

    @Query("SELECT NEW com.tuempresa.creditflow.creditflow_api.dto.kyc.KycVerifiedCompanyResponseDTO(" +
           "k.company.id, k.company.company_name, k.status, k.idKyc) " +
           "FROM KycVerification k " +
           "WHERE k.company IS NOT NULL")
    List<KycVerifiedCompanyResponseDTO> findVerifiedCompanyDetails();

    /**
     * Consulta JPQL para filtrar por KycEntityType y/o Status.
     * Si un parámetro es NULL, se ignora ese filtro.
     */
    @Query("SELECT k FROM KycVerification k " +
            "WHERE (:kycEntityType IS NULL OR k.entityType = :kycEntityType) " +
            "AND (:status IS NULL OR k.status = :status)")
    List<KycVerification> findFiltered(
            KycEntityType kycEntityType,
            KycStatus status
    );

    Optional<KycVerification> findByCompanyId(UUID companyId);

    boolean existsByUserIdAndEntityType(UUID userId, KycEntityType kycEntityType);
}
