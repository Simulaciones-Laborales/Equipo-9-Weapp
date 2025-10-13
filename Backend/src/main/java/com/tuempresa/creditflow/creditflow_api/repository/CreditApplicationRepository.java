package com.tuempresa.creditflow.creditflow_api.repository;

import com.tuempresa.creditflow.creditflow_api.model.CreditApplication;
import com.tuempresa.creditflow.creditflow_api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, UUID> {

    List<CreditApplication> findByCompany(Company company);

    Optional<CreditApplication> findByIdAndCompany(UUID id, Company company);

    // Buscar por company id (sin cargar la entidad company) — útil para endpoints públicos
    List<CreditApplication> findByCompany_Id(UUID companyId);
}
