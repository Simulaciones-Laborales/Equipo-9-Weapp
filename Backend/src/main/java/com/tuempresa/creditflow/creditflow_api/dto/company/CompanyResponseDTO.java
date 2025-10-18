package com.tuempresa.creditflow.creditflow_api.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO que contiene la información detallada de una empresa para ser devuelta al cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Estructura de datos que contiene la información completa de una empresa, incluyendo metadatos de creación.",
    example = "{" +
        "\"idCompany\": \"a1b2c3d4-e5f6-7890-1234-567890abcdef\"," +
        "\"name\": \"Tech Innovators S.A.\"," +
        "\"taxId\": \"A12345678\"," +
        "\"annualIncome\": 500000.00," +
        "\"createdAt\": \"2025-10-15T10:30:00\"," +
        "\"userId\": \"f0e9d8c7-b6a5-4321-fedc-ba9876543210\"," +
        "\"userName\": \"admin.user\"" +
    "}"
)
public class CompanyResponseDTO {

    @Schema(
        description = "Identificador único (UUID) de la empresa.",
        example = "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        format = "uuid"
    )
    private UUID idCompany;

    @Schema(
        description = "Nombre completo o razón social de la empresa.",
        example = "Tech Innovators S.A."
    )
    private String name;

    @Schema(
        description = "Identificador fiscal (ej. RUC, NIF, CIF) de la empresa.",
        example = "A12345678"
    )
    private String taxId;

    @Schema(
        description = "Ingreso anual de la empresa.",
        example = "500000.00"
    )
    private BigDecimal annualIncome;

    @Schema(
        description = "Fecha y hora de creación del registro de la empresa.",
        example = "2025-10-15T10:30:00",
        format = "date-time"
    )
    private LocalDateTime createdAt;

    @Schema(
        description = "Identificador único (UUID) del usuario que creó el registro.",
        example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210",
        format = "uuid"
    )
    private UUID userId;

    @Schema(
        description = "Nombre de usuario o alias de la persona que creó el registro.",
        example = "admin.user"
    )
    private String userName;
}
