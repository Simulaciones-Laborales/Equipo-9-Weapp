package com.tuempresa.creditflow.creditflow_api.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class KycVerificationRequestDTO {
    private UUID userId;
}