package com.tuempresa.creditflow.creditflow_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.UUID;

public record UserUpdateRequestDto(

        UUID userId,

        @Schema(description = "Nombre de usuario", example = "LucianoMO")
        String firstName,

        @Schema(description = "Apellidos del usuario", example = "Cortez")
        String lastName,

        @Schema(description = "Contacto del usuario", example = "+1522540454")
        String contact,

        @Schema(
                description = "Fecha de nacimiento del usuario en formato dd/MM/yyyy",
                example = "15/05/1990"
        )
        @NotBlank(message = "La fecha de nacimiento no puede estar en blanco")
        @Pattern(
                regexp = "^([0-2][0-9]|3[0-1])/([0][1-9]|1[0-2])/\\d{4}$",
                message = "La fecha de nacimiento debe tener el formato dd/MM/yyyy"
        )
        String birthDate,

        @Schema(
                description = "País de residencia del usuario",
                example = "Argentina"
        )
        @NotBlank(message = "El país no puede estar en blanco")
        @Size(max = 100, message = "El país no puede exceder los 100 caracteres")
        String country

) implements Serializable {
}
