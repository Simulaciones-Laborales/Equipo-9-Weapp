package com.tuempresa.creditflow.creditflow_api.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record LoginRequestDto(

        @Schema(description = "Email electrónico del usuario", example = "Florencia_Galeassi@example.com")
        @Email(message = "El correo electrónico debe ser valido, utilizando ´@´")
        @NotBlank(message = "El correo electrónico no puede estar en blanco")
        String email,

        @Schema(description = "Contraseña del usuario", example = "12345678Pro+")
        @NotBlank(message = "La contraseña no puede estar en blanco")
        String password

) implements Serializable {
}