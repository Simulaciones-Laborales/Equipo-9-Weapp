package com.tuempresa.creditflow.creditflow_api.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tuempresa.creditflow.creditflow_api.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        @Schema(description = "ID único del usuario", example = "d96d6e88-4ade-4f62-98d7-235ea23f6f2a")
        UUID id,
        @Schema(description = "Nombre de usuario", example = "LucianoEM")
        String username,
        @Schema(description = "Correo electrónico del usuario", example = "lucianomolina970@gmail.com")
        String email,
        @Schema(description = "Número de contacto del usuario", example = "015-22540454")
        String contact,
        @Schema(description = "Rol del usuario", example = "USER")
        User.Role role,
        @Schema(description = "Estado del usuario", example = "true")
        Boolean isActive,
        @Schema(description = "Fecha de creación del usuario", example = "25-04-2025 10:39:45")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
        LocalDateTime createdAt,
        @Schema(description = "Fecha de actualización del usuario", example = "25-04-2025 10:42:17")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
        LocalDateTime updatedAt

) implements Serializable {
}
