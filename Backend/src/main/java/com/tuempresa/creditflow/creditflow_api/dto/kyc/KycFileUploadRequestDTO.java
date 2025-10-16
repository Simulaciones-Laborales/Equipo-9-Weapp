package com.tuempresa.creditflow.creditflow_api.dto.kyc;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record KycFileUploadRequestDTO(
        UUID userId,
        MultipartFile selfie,
        MultipartFile dniFront,
        MultipartFile dniBack
) {}