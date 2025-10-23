package com.tuempresa.creditflow.creditflow_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Documento asociado a una solicitud de crédito.
 * Cada documento puede tener un impacto (positivo o negativo)
 * sobre el puntaje de riesgo total de la solicitud.
 */
@Entity
@Table(name = "risk_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id")
    @JsonBackReference
    private CreditApplication creditApplication;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "document_url")
    private String documentUrl;

    @Schema(
            description = "Puntaje de riesgo del documento, calculado automáticamente usando OCR + ML. Rango: 0 (mínimo) - 100 (máximo). Interpretación: 0-25 Bajo, 26-50 Moderado, 51-75 Alto, 76-100 Muy alto",
            example = "72"
    )
    private Integer scoreImpact;
}

