package com.tuempresa.creditflow.creditflow_api.dtos.user;

import com.tuempresa.creditflow.creditflow_api.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

public record AuthResponseDto(
        @Schema(description = "ID del usuario", example = "d96d6e88-4ade-4f62-98d7-235ea23f6f2a")
        UUID id,

        @Schema(description = "Nombre de usuario", example = "Florencia")
        String firstName,

        @Schema(description = "Apellidos de usuario", example = "Rodríguez")
        String lastName,

        @Schema(description = "Token de autenticación JWT", example = "eyJhbGciOiJIUzUxMiJ9...")
        String token,

        @Schema(description = "Rol del usuario", example = "USUARIO")
        User.Role role

) implements Serializable {
}
