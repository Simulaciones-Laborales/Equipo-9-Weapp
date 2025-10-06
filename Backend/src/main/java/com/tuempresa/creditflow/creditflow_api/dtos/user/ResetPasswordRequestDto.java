package com.tuempresa.creditflow.creditflow_api.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public record ResetPasswordRequestDto(
        @Schema(description = "Token generado para el reseteo de contraseña", example = "e0b95e8f-ae13-4f28-b98b-d5530d4ba1e9")
        String token,
        @Schema(description = "Nueva contraseña del usuario", example = "123456789Mar+")
        String newPassword

) implements Serializable {
}
