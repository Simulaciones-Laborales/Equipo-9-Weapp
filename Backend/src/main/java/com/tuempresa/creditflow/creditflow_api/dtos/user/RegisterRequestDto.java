package com.tuempresa.creditflow.creditflow_api.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.io.Serializable;

public record RegisterRequestDto(
        @Schema(
                description = "Nombre de usuario con al menos 3 letras, puede incluir espacios, apóstrofes o guiones.",
                example = "Ignacio Galeassi"
        )
        @Pattern(
                regexp = "^(?=\\S*[a-zA-ZÀ-ÿ])(?=(?:\\S*\\s*){3,})[a-zA-ZÀ-ÿ\\s'-]+$",
                message = "El nombre de usuario debe tener al menos 3 letras y puede incluir espacios, apóstrofes o guiones"
        )
        @NotBlank(message = "El nombre de usuario no puede estar en blanco")
        @Size(max = 50, message = "El nombre de usuario no puede exceder los 50 caracteres")
        String username,

        @Schema(
                description = "Correo electrónico válido.",
                example = "Ignacio_Galeassi@example.com"
        )
        @Pattern(
                regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$",
                message = "El correo electrónico debe ser válido y contener un dominio correcto"
        )
        @Email(message = "El correo electrónico debe ser valido, utilizando ´@´")
        @NotBlank(message = "El correo electrónico no puede estar en blanco")
        @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres")
        String email,

        @Schema(
                description = "Número de contacto válido que solo contiene dígitos.",
                example = "+54 351-2854563"
        )
        @Pattern(
                regexp = "^\\+?\\d{1,4}[\\s-]?\\d{1,4}[\\s-]?\\d{4,10}$",
                message = "El contacto debe estar en formato válido, por ejemplo: +54 351-2854563"
        )
        @NotBlank(message = "El contacto no puede estar en blanco")
        @Size(max = 20, message = "El contacto no puede exceder los 20 caracteres")
        String contact


) implements Serializable {
}
