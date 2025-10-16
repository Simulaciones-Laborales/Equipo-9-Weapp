package com.tuempresa.creditflow.creditflow_api.dto.history;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO que representa un evento o registro en el historial de una solicitud de crédito.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Registro de un evento o cambio de estado dentro del ciclo de vida de una solicitud de crédito.",
    example = "{" +
        "\"id\": \"a0b1c2d3-e4f5-6789-abcd-ef0123456789\"," +
        "\"creditApplicationId\": \"11223344-5566-7788-99aa-bbccddeeff00\"," +
        "\"actionType\": \"STATUS_CHANGE\"," +
        "\"action\": \"APPROVED\"," +
        "\"comments\": \"Aprobado por sistema de scoring.\"," +
        "\"operatorId\": \"f0e9d8c7-b6a5-4321-fedc-ba9876543210\"," +
        "\"operatorName\": \"System.Auto\"," +
        "\"createdAt\": \"2025-10-16T09:30:00\"" +
    "}"
)
public class CreditApplicationHistoryDTO {

    @Schema(
        description = "Identificador único (UUID) del registro de historial.",
        example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789",
        format = "uuid"
    )
    private UUID id;

    @Schema(
        description = "Identificador único (UUID) de la solicitud de crédito a la que pertenece este registro.",
        example = "11223344-5566-7788-99aa-bbccddeeff00",
        format = "uuid"
    )
    private UUID creditApplicationId;

    @Schema(
        description = "Tipo de acción que generó el registro.",
        example = "STATUS_CHANGE",
        // Actualización clave: Lista de todos los posibles tipos de acción
        allowableValues = {"CREATION", "UPDATE", "STATUS_CHANGE", "COMMENT", "OPERATOR_ACTION", "AUTOMATION", "APPROVAL", "REJECTION", "CANCELLATION", "DELETION"}
    )
    private String actionType;

    @Schema(
        description = "Detalle de la acción realizada (ej. el nuevo estado si actionType es STATUS_CHANGE, o el nombre del campo actualizado).",
        example = "APPROVED"
    )
    private String action;

    @Schema(
        description = "Comentarios adicionales o la razón del registro.",
        example = "Aprobado por sistema de scoring."
    )
    private String comments;

    @Schema(
        description = "Identificador único (UUID) del operador, usuario o sistema que realizó la acción.",
        example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210",
        format = "uuid"
    )
    private UUID operatorId;

    @Schema(
        description = "Nombre o alias del operador, usuario o sistema que realizó la acción.",
        example = "System.Auto"
    )
    private String operatorName;

    @Schema(
        description = "Fecha y hora en que ocurrió la acción y se creó el registro.",
        example = "2025-10-16T09:30:00",
        format = "date-time"
    )
    private LocalDateTime createdAt;
}
