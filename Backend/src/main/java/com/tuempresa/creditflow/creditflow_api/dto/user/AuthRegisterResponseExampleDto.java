package com.tuempresa.creditflow.creditflow_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ejemplo completo de respuesta al registrar usuario")
public record AuthRegisterResponseExampleDto(

        @Schema(description = "Indica si la respuesta contiene un error", example = "false")
        boolean isError,

        @Schema(description = "Código HTTP devuelto", example = "201")
        int code,

        @Schema(description = "Estado HTTP textual", example = "Created")
        String status,

        @Schema(description = "Mensaje descriptivo de la operación", example = "Usuario creado correctamente. El correo de bienvenida ha sido procesado.")
        String message,

        @Schema(description = "Datos del usuario autenticado", implementation = AuthResponseDto.class)
        AuthResponseDto data
) {
}
