package com.tuempresa.creditflow.creditflow_api.repository;

import com.tuempresa.creditflow.creditflow_api.model.Company;
import com.tuempresa.creditflow.creditflow_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findByUser(User user);

    boolean existsByTaxId(String taxId);

    Optional<Company> findByTaxId(String taxId);

    void deleteByIdAndUser(UUID id, User user);

    List<Company> findByUserId(UUID id);

    Optional<Company> findByIdAndUser(UUID id, User user);
}

