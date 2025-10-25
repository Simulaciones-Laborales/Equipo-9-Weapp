package com.tuempresa.creditflow.creditflow_api.dto.kyc;

import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import lombok.Value; 
import java.util.UUID;

// ------------------- Anotación de Swagger a Nivel de Clase -------------------
import io.swagger.v3.oas.annotations.media.Schema; 

@Value 
@Schema(description = "DTO de respuesta que combina los datos básicos de una Compañía con el estado de su Verificación KYC.")
public class KycVerifiedCompanyResponseDTO {

    // ------------------- Anotaciones de Swagger a Nivel de Campo -------------------
    
    @Schema(description = "Identificador único (UUID) de la Compañía.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    UUID companyId;

    @Schema(description = "Nombre comercial o razón social de la Compañía.", example = "Acme S.A.S.")
    String companyName;

    @Schema(description = "Estatus actual del proceso de verificación KYC.", example = "PENDING")
    KycStatus verificationStatus;

    @Schema(description = "Identificador único (UUID) del registro de Verificación KYC.", example = "f9e8d7c6-b5a4-3210-fedc-ba9876543210")
    UUID verificationId;
}
