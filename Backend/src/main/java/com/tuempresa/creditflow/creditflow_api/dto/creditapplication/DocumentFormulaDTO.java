package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Documento o parámetro utilizado en la fórmula de evaluación crediticia.")
public class DocumentFormulaDTO {

    @Schema(description = "Nombre del documento o variable de fórmula.", example = "balance_general_2024")
    private String name;

    @Schema(description = "Tipo de documento o categoría (por ejemplo, financiero, fiscal, etc.).", example = "financiero")
    private String type;

    @Schema(description = "Valor o puntuación asociado al documento o fórmula.", example = "85.5")
    private Double value;
}
