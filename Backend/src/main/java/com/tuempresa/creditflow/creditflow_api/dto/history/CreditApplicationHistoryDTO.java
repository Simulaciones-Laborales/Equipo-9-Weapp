package com.tuempresa.creditflow.creditflow_api.dto.history;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditApplicationHistoryDTO {

    private UUID id;
    private UUID creditApplicationId;
    private String actionType;
    private String action;
    private String comments;
    private UUID operatorId;
    private String operatorName;
    private LocalDateTime createdAt;
}

