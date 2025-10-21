package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para la solicitud de cambio de estado de una solicitud de crédito.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Objeto de datos utilizado para actualizar el estado de una solicitud de crédito.",
    example = "{\"newStatus\": \"APPROVED\", \"comments\": \"Riesgo aceptable después de revisión manual.\"}"
)
public class CreditApplicationStatusChangeDTO {

    @Schema(
        description = "El nuevo estado que se le asignará a la solicitud. Debe ser un valor válido del enumerador CreditStatus.",
        example = "APPROVED",
        // Si tienes los valores exactos del enum, puedes incluirlos aquí para mayor claridad.
        allowableValues = {"PENDING", "UNDER_REVIEW", "APPROVED", "REJECTED", "CANCELLED"}
    )
    @NotBlank
    private String newStatus; // debe corresponder al enum CreditStatus

    @Schema(
        description = "Comentarios opcionales sobre la razón del cambio de estado. Este comentario se registrará en el historial de la solicitud.",
        example = "Riesgo aceptable después de revisión manual."
    )
    private String comments; // comentario que irá al history y/o al operatorComments
}
