package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO que contiene la información completa y el estado actual de una solicitud de crédito.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Estructura de datos que contiene todos los detalles de una solicitud de crédito, incluyendo su estado actual y metadatos de tiempo.",
    example = "{" +
        "\"idCreditApplication\": \"11223344-5566-7788-99aa-bbccddeeff00\"," +
        "\"companyId\": \"a1b2c3d4-e5f6-7890-1234-567890abcdef\"," +
        "\"companyName\": \"Tech Innovators S.A.\"," +
        "\"amount\": 25000.00," +
        "\"status\": \"UNDER_REVIEW\"," +
        "\"operatorComments\": \"Pendiente de adjuntar documentación fiscal.\"," +
        "\"createdAt\": \"2025-10-15T10:00:00\"," +
        "\"updatedAt\": \"2025-10-16T09:30:00\"" +
    "}"
)
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

    @Schema(
        description = "Últimos comentarios internos añadidos por el operador/analista sobre el estado de la solicitud.",
        example = "Pendiente de adjuntar documentación fiscal."
    )
    private String operatorComments;

    @Schema(
        description = "Fecha y hora de creación de la solicitud.",
        example = "2025-10-15T10:00:00",
        format = "date-time"
    )
    private LocalDateTime createdAt;

    @Schema(
        description = "Fecha y hora de la última actualización del estado o datos de la solicitud.",
        example = "2025-10-16T09:30:00",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
}
