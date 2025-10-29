package com.tuempresa.creditflow.creditflow_api.dto.kyc;

import lombok.Data;

import java.util.UUID;

@Data
public class KycVerificationRequestCompanyDTO {
    private UUID companyId;
}
