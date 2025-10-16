package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationRequestDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationResponseDTO;
import com.tuempresa.creditflow.creditflow_api.dto.creditapplication.CreditApplicationStatusChangeDTO;
import com.tuempresa.creditflow.creditflow_api.model.CreditStatus;
import com.tuempresa.creditflow.creditflow_api.model.User;

import java.util.List;
import java.util.UUID;

public interface CreditApplicationService {

    /**
     * Crea una nueva solicitud de crédito asociada a una empresa y un usuario.
     *
     * @param dto  Datos de la solicitud.
     * @param user Usuario autenticado que crea la solicitud.
     * @return DTO con la información de la solicitud creada.
     */
    CreditApplicationResponseDTO createApplication(CreditApplicationRequestDTO dto, User user);

    /**
     * Obtiene una solicitud de crédito específica por su ID,
     * solo si pertenece al usuario autenticado.
     *
     * @param id   ID de la solicitud de crédito.
     * @param user Usuario autenticado.
     * @return DTO con la información de la solicitud.
     */
    CreditApplicationResponseDTO getApplicationByIdAndUser(UUID id, User user);

    /**
     * Lista todas las solicitudes de crédito de una empresa del usuario autenticado.
     *
     * @param companyId ID de la empresa.
     * @param user      Usuario autenticado.
     * @return Lista de DTOs con las solicitudes de crédito.
     */
    List<CreditApplicationResponseDTO> getApplicationsByCompany(UUID companyId, User user);

    /**
     * Actualiza una solicitud de crédito (monto y comentarios del operador).
     *
     * @param id   ID de la solicitud.
     * @param dto  Datos actualizados.
     * @param user Usuario autenticado.
     * @return DTO con la información actualizada.
     */
    CreditApplicationResponseDTO updateApplication(UUID id, CreditApplicationRequestDTO dto, User user);

    /**
     * Cambia el estado de una solicitud de crédito (por operador o admin).
     *
     * @param id   ID de la solicitud.
     * @param dto  Objeto con el nuevo estado y comentarios.
     * @param user Usuario autenticado que realiza la acción.
     * @return DTO actualizado con el nuevo estado.
     */
    CreditApplicationResponseDTO changeStatus(UUID id, CreditApplicationStatusChangeDTO dto, User user);

    /**
     * Elimina una solicitud de crédito (si pertenece al usuario o el usuario es admin).
     *
     * @param id   ID de la solicitud.
     * @param user Usuario autenticado.
     */
    void deleteApplication(UUID id, User user);

}

