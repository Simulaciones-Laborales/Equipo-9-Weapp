package com.tuempresa.creditflow.creditflow_api.dto.user;

import com.tuempresa.creditflow.creditflow_api.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

public record UserRolDto(
        @Schema(description = "ID único del usuario", example = "d96d6e88-4ade-4f62-98d7-235ea23f6f2a")
        UUID id,
        @Schema(description = "Nombre de usuario", example = "LucianoEM")
        String username,
        @Schema(
                description = "Correo electrónico válido.",
                example = "Valentina_Galeassi@example.com"
        )
        String email,
        @Schema(description = "Estado del usuario", example = "true")
        Boolean isActive,
        @Schema(description = "Rol del usuario", example = "ADMIN_DE_CONTENIDO")
        User.Role role
) implements Serializable {
}
