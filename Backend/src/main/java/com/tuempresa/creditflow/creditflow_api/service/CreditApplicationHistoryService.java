package com.tuempresa.creditflow.creditflow_api.service;

import com.tuempresa.creditflow.creditflow_api.dto.history.CreditApplicationHistoryDTO;
import com.tuempresa.creditflow.creditflow_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CreditApplicationHistoryService {

    /**
     * Obtiene el historial paginado de una solicitud de crédito.
     * Verifica que el requester tenga acceso (propietario oper/admin).
     */
    Page<CreditApplicationHistoryDTO> getHistoryByApplication(UUID creditApplicationId, User requester, Pageable pageable);

    /**
     * Crea una nueva entrada de historial para una solicitud.
     * Normalmente esto se llamará desde otros servicios (ej. cuando se cambia estado).
     */
    CreditApplicationHistoryDTO addHistoryEntry(UUID creditApplicationId, CreditApplicationHistoryDTO dto, User operator);
}

