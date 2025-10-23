package com.tuempresa.creditflow.creditflow_api.dto.kyc;

import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import lombok.Data;

@Data
public class KycStatusUpdateDTO {
    private KycStatus status;
    private String notes;
}
