package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dtos.company.CompanyRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dtos.company.CompanyResponseDTO;
import com.tuempresa.creditflow.creditflow_api.model.User;

import java.util.List;
import java.util.UUID;

public interface CompanyService {

    /**
     * Crea una nueva company asociada al usuario proporcionado.
     * Puede lanzar IllegalArgumentException (o una excepción personalizada) si ya existe taxId duplicado.
     */
    CompanyResponseDTO createCompany(CompanyRequestDTO dto, User user);

    /**
     * Lista las companies del usuario.
     */
    List<CompanyResponseDTO> getCompaniesByUser(User user);

    /**
     * Obtiene una company por id validando que pertenezca al usuario.
     * Debe lanzar una excepción si la company no existe o no pertenece al user.
     */
    CompanyResponseDTO getCompanyByIdAndUser(UUID id, User user);

    /**
     * Actualiza una company (solo si pertenece al usuario).
     */
    CompanyResponseDTO updateCompany(UUID id, CompanyRequestDTO dto, User user);

    /**
     * Elimina una company (solo si pertenece al usuario).
     */
   /* void deleteCompany(UUID id, User user);*/
}



