package com.tuempresa.creditflow.creditflow_api.dto.bcra;

public record BcraDeudasResponseDTO(
        Integer status,
        BcraDeudasResults results
) {}
