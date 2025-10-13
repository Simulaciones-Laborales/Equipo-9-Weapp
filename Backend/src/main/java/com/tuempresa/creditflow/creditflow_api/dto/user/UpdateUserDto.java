package com.tuempresa.creditflow.creditflow_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.UUID;

public record UpdateUserDto(
        @Schema(description = "ID único del usuario", example = "d96d6e88-4ade-4f62-98d7-235ea23f6f2a")
        UUID userId,
        @Schema(description = "Nombre de usuario", example = "Luciano Molina")
        @Pattern(
                regexp = "^(?=\\S*[a-zA-ZÀ-ÿ])(?=(?:\\S*\\s*){3,})[a-zA-ZÀ-ÿ\\s'-]+$",
                message = "El nombre de usuario debe tener al menos 3 letras y puede incluir espacios, apóstrofes o guiones"
        )
        String username,
        @Schema(description = "Nombre de usuario", example = "LucianoMO")
        String firstName,
        @Schema(description = "Apellidos del usuario", example = "Cortez")
        String lastName,
        @Schema(description = "Correo electrónico del usuario", example = "lucianoFront23@gmail.com")
        String email,
        @Schema(description = "Contacto del usuario", example = "+3515846563")
        String contact,
        @Schema(description = "Contraseña del usuario", example = "123456780Pro+ (No Encriptado)")
        String password

) implements Serializable {
}
