package com.tuempresa.creditflow.creditflow_api.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class KycVerificationRequestDTO {
    private UUID userId;
}
