package com.tuempresa.creditflow.creditflow_api.dtos;

import com.tuempresa.creditflow.creditflow_api.model.KycStatus;
import lombok.Data;

@Data
public class KycStatusUpdateDTO {
    private KycStatus status;
    private String notes;
}