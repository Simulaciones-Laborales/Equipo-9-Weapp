package com.tuempresa.creditflow.creditflow_api.repository;

import com.tuempresa.creditflow.creditflow_api.model.KycVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KycVerificationRepository extends JpaRepository<KycVerification, UUID> {
    List<KycVerification> findByUserId(UUID userId);
}