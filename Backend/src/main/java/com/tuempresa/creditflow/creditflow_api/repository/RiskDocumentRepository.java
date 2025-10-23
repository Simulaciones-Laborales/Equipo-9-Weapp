package com.tuempresa.creditflow.creditflow_api.repository;

import com.tuempresa.creditflow.creditflow_api.model.CreditApplication;
import com.tuempresa.creditflow.creditflow_api.model.RiskDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RiskDocumentRepository extends JpaRepository<RiskDocument, UUID> {
    List<RiskDocument> findByCreditApplication(CreditApplication app);
}
