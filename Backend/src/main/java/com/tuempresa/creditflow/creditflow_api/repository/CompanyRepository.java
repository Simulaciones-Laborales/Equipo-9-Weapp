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

    // Buscar todas las empresas de un usuario
    List<Company> findByUser(User user);

    // Comprobar existencia por taxId
    boolean existsByTaxId(String taxId);

    // Buscar por taxId (útil para validaciones o consultas)
    Optional<Company> findByTaxId(String taxId);

    // (Opcional) Eliminar por id y user — útil para borrar solo si pertenece al user
    void deleteByIdAndUser(UUID id, User user);

    List<Company> findByUserId(UUID id);
}

