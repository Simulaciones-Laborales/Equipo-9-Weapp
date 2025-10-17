package com.tuempresa.creditflow.creditflow_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public record ChangeUserRoleDto(
        @Schema(description = "ID único del usuario", example = "d96d6e88-4ade-4f62-98d7-235ea23f6f2a")
        @NotNull(message = "El ID único del Usuario no puede estar en blanco")
        UUID id,
        @Schema(description = "Modifica un Rol (SUPER_ADMIN, ADMIN_DE_CONTENIDO, USUARIO, CRM)", example = "ADMIN_DE_CONTENIDO")
        @NotBlank(message = "El Rol del usuario no puede estar en blanco")
        String role
) implements Serializable {
}
