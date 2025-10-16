package com.tuempresa.creditflow.creditflow_api.dto.kyc;

import com.tuempresa.creditflow.creditflow_api.model.KycStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class KycVerificationResponseDTO {
    private UUID idKyc;
    private KycStatus status;
    private String verificationNotes;
    private String externalReferenceId;
    private LocalDateTime submissionDate;
    private LocalDateTime verificationDate;
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private String selfieUrl;
    private String dniFrontUrl;
    private String dniBackUrl;
}
