package com.tuempresa.creditflow.creditflow_api.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public record UserStatusRequestDto(
        @Schema(description = "ID del usuario", example = "d96d6e88-4ade-4f62-98d7-235ea23f6f2a")
        @NotNull(message = "El ID del usuario no puede estar en blanco")
        UUID id,
        @Schema(description = "Estado del usuario", example = "true or false")
        @NotNull(message = "El estado del usuario no puede estar en blanco")
        Boolean status
) implements Serializable {
}
