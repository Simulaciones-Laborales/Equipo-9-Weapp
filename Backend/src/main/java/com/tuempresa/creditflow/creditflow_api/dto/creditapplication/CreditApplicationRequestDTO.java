package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para la solicitud de creación de una nueva solicitud de crédito.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Datos necesarios para registrar una nueva solicitud de crédito.",
    example = "{" +
        "\"companyId\": \"a1b2c3d4-e5f6-7890-1234-567890abcdef\"," +
        "\"amount\": 25000.00," +
        "\"operatorComments\": \"Evaluación inicial por scoring automático.\"" +
    "}"
)
public class CreditApplicationRequestDTO {

    @Schema(
        description = "Identificador único (UUID) de la empresa para la que se solicita el crédito.",
        example = "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        required = true,
        format = "uuid"
    )
    @NotNull
    private UUID companyId; // identificador de la empresa solicitante

    @Schema(
        description = "Monto del crédito solicitado. Debe ser un valor positivo.",
        example = "25000.00",
        required = true,
        minimum = "0.01" // Implica positividad
    )
    @NotNull
    @Positive
    private BigDecimal amount;

    @Schema(
        description = "Comentario inicial opcional sobre la solicitud, típicamente para uso del operador/analista.",
        example = "Evaluación inicial por scoring automático.",
        required = false
    )
    // Comentario inicial opcional que quedará en operatorComments
    private String operatorComments;
}
