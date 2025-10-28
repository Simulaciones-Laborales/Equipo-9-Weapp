package com.tuempresa.creditflow.creditflow_api.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para la solicitud de creación o actualización de una empresa.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Anotación principal para describir el objeto DTO en Swagger/OpenAPI
@Schema(
    description = "Datos necesarios para crear o actualizar la información de una empresa.",
    example = "{\"name\": \"Tech Innovators S.A.\", \"taxId\": \"20948079365\", \"annualIncome\": 500000.00}"
)
public class CompanyRequestDTO {

    // Anotación para describir el campo 'name'
    @Schema(
        description = "Nombre completo o razón social de la empresa.",
        example = "Tech Innovators S.A.",
        maxLength = 100,
        required = true
    )
    @NotBlank
    @Size(max = 100)
    private String name;

    // Anotación para describir el campo 'taxId'
    @Schema(
        description = "Identificador fiscal (ej. RUC, NIF, CIF) de la empresa.",
        example = "20948079365",
        maxLength = 50,
        required = true
    )
    @NotBlank
    @Size(max = 50)
    private String taxId;

    // Anotación para describir el campo 'annualIncome'
    @Schema(
        description = "Ingreso anual de la empresa en la moneda local. Debe ser un valor positivo o cero.",
        example = "500000.00",
        required = true,
        minimum = "0.00"
    )
    @NotNull
    @PositiveOrZero
    private BigDecimal annualIncome;
}
