package com.tuempresa.creditflow.creditflow_api.dto.creditapplication;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskDocumentDTO {
    private UUID id;
    private String name;
    private String documentUrl; // puede ser la URL
    private Integer scoreImpact;
}