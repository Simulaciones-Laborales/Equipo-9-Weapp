package com.tuempresa.creditflow.creditflow_api.repository;

import com.tuempresa.creditflow.creditflow_api.model.CreditApplicationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CreditApplicationHistoryRepository extends JpaRepository<CreditApplicationHistory, UUID> {

    // Pageado por credit application id, ordenado desc por createdAt (se puede implementar con query method)
    Page<CreditApplicationHistory> findByCreditApplication_IdOrderByCreatedAtDesc(UUID creditApplicationId, Pageable pageable);
}

