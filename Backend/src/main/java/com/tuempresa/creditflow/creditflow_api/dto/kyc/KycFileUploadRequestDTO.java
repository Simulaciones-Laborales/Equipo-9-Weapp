package com.tuempresa.creditflow.creditflow_api.dto.kyc;

import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record KycFileUploadRequestDTO(
        UUID entityId,                 // puede ser userId o companyId
        KycEntityType entityType,      // USER o COMPANY
        MultipartFile document1Url,
        MultipartFile document2Url,
        MultipartFile document3Url
) {}