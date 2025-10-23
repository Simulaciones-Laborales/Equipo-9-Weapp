package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dto.history.CreditApplicationHistoryDTO;
import com.tuempresa.creditflow.creditflow_api.enums.CreditApplicationActionType;
import com.tuempresa.creditflow.creditflow_api.exception.ResourceNotFoundException;
import com.tuempresa.creditflow.creditflow_api.mapper.CreditApplicationHistoryMapper;
import com.tuempresa.creditflow.creditflow_api.model.CreditApplication;
import com.tuempresa.creditflow.creditflow_api.model.CreditApplicationHistory;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.repository.CreditApplicationHistoryRepository;
import com.tuempresa.creditflow.creditflow_api.repository.CreditApplicationRepository;
import com.tuempresa.creditflow.creditflow_api.service.CreditApplicationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditApplicationHistoryServiceImpl implements CreditApplicationHistoryService {

    private final CreditApplicationHistoryRepository historyRepository;
    private final CreditApplicationRepository creditApplicationRepository;

    /**
     * Verifica existencia y acceso, luego devuelve página de historial mapeada a DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CreditApplicationHistoryDTO> getHistoryByApplication(UUID creditApplicationId, User requester, Pageable pageable) {
        CreditApplication app = creditApplicationRepository.findById(creditApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit application not found: " + creditApplicationId));

        boolean isOwner = app.getCompany() != null && app.getCompany().getUser() != null
                && app.getCompany().getUser().getId().equals(requester.getId());

        boolean isOperatorOrAdmin = requester.getRole() != null &&
                (requester.getRole().name().equals("ADMIN") || requester.getRole().name().equals("OPERADOR"));

        if (!isOwner && !isOperatorOrAdmin) {
            throw new ResourceNotFoundException("Credit application not found or not accessible");
        }

        return historyRepository.findByCreditApplication_IdOrderByCreatedAtDesc(creditApplicationId, pageable)
                .map(CreditApplicationHistoryMapper::toDTO);
    }

    /**
     * Crea una entrada de historial para la solicitud indicada.
     * Se utiliza dentro de transacciones externas normalmente, pero aquí la marcamos @Transactional.
     */
    @Override
    @Transactional
    public CreditApplicationHistoryDTO addHistoryEntry(UUID creditApplicationId, CreditApplicationHistoryDTO dto, User operator) {
        CreditApplication app = creditApplicationRepository.findById(creditApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit application not found: " + creditApplicationId));

        CreditApplicationHistory h = CreditApplicationHistory.builder()
                .creditApplication(app)
                .actionType(
                        dto.getActionType() != null
                                ? CreditApplicationActionType.valueOf(dto.getActionType())
                                : CreditApplicationActionType.OPERATOR_ACTION
                )
                .action(dto.getAction())
                .comments(dto.getComments())
                .operator(operator)
                .build();

        CreditApplicationHistory saved = historyRepository.save(h);
        return CreditApplicationHistoryMapper.toDTO(saved);
    }
}

