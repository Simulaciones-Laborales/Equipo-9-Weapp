package com.tuempresa.creditflow.creditflow_api.mapper;

import com.tuempresa.creditflow.creditflow_api.dto.history.CreditApplicationHistoryDTO;
import com.tuempresa.creditflow.creditflow_api.model.CreditApplicationHistory;

public class CreditApplicationHistoryMapper {

    public static CreditApplicationHistoryDTO toDTO(CreditApplicationHistory history) {
        return CreditApplicationHistoryDTO.builder()
                .id(history.getId())
                .creditApplicationId(history.getCreditApplication() != null ? 
                                     history.getCreditApplication().getId() : null)
                .actionType(history.getActionType() != null ? history.getActionType().name() : null)
                .action(history.getAction())
                .comments(history.getComments())
                .operatorId(history.getOperator() != null ? history.getOperator().getId() : null)
                .operatorName(history.getOperator() != null ? history.getOperator().getName() : null)
                .createdAt(history.getCreatedAt())
                .build();
    }
}

