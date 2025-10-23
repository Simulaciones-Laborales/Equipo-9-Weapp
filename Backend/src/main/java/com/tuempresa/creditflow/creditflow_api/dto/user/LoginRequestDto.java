package com.tuempresa.creditflow.creditflow_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record LoginRequestDto(

        @Schema(description = "Email electrónico del usuario", example = "operador1@creditflow.com")
        @Email(message = "El correo electrónico debe ser valido, utilizando ´@´")
        @NotBlank(message = "El correo electrónico no puede estar en blanco")
        String email,

        @Schema(description = "Contraseña del usuario", example = "Pass1234!")
        @NotBlank(message = "La contraseña no puede estar en blanco")
        String password

) implements Serializable {
}
