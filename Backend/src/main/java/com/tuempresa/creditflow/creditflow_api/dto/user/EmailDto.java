package com.tuempresa.creditflow.creditflow_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public record EmailDto(
        @Schema(description = "Correo electrónico válido.", example = "Florencia_Galeassi@example.com")
        String email

) implements Serializable {
}

