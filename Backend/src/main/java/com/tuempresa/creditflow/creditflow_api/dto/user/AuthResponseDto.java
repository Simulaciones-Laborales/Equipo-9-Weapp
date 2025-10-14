package com.tuempresa.creditflow.creditflow_api.dto.user;

import com.tuempresa.creditflow.creditflow_api.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

public record AuthResponseDto(

        UUID id,

        @Schema(description = "Nombre de usuario", example = "Jorge")
        String firstName,

        @Schema(description = "Apellidos de usuario", example = "Rodríguez")
        String lastName,

        @Schema(description = "Nombres y Apellidos del usuario", example = "Jorge Rodríguez")
        String username,

        @Schema(description = "Email de usuario", example = "usuario@dominio.com")
        String email,

        @Schema(description = "Token de autenticación JWT", example = "eyJhbGciOiJIUzUxMiJ9...")
        String token,

        @Schema(description = "Rol del usuario", example = "USUARIO")
        User.Role role

) implements Serializable {
}
