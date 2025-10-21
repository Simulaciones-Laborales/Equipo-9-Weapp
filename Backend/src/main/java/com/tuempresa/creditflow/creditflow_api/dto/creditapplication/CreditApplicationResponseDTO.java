package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import com.tuempresa.creditflow.creditflow_api.enums.CreditPurpose;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO que contiene la información completa y el estado actual de una solicitud de crédito.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreditApplicationResponseDTO {

    @Schema(
        description = "Identificador único (UUID) de la solicitud de crédito.",
        example = "11223344-5566-7788-99aa-bbccddeeff00",
        format = "uuid"
    )
    private UUID idCreditApplication;

    @Schema(
        description = "Identificador único (UUID) de la empresa solicitante.",
        example = "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        format = "uuid"
    )
    private UUID companyId;

    @Schema(
        description = "Nombre de la empresa que realizó la solicitud.",
        example = "Tech Innovators S.A."
    )
    private String companyName;

    @Schema(
        description = "Monto del crédito solicitado.",
        example = "25000.00"
    )
    private BigDecimal amount;

    @Schema(
        description = "Estado actual de la solicitud de crédito (valores posibles: PENDING, UNDER_REVIEW, APPROVED, REJECTED, CANCELLED).",
        example = "UNDER_REVIEW"
    )
    private String status;
    private Integer termMonths;
    private CreditPurpose creditPurpose;
    private LocalDateTime createdAt;

    @Schema(
        description = "Fecha y hora de la última actualización del estado o datos de la solicitud.",
        example = "2025-10-16T09:30:00",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
    private Integer riskScore;
    private List<RiskDocumentDTO> documents; // 👈 ahora usamos DTOs de documentos
}


