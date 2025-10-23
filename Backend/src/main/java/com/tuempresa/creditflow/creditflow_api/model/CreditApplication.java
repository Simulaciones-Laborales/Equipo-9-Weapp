package com.tuempresa.creditflow.creditflow_api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tuempresa.creditflow.creditflow_api.enums.CreditPurpose;
import com.tuempresa.creditflow.creditflow_api.enums.CreditStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa una solicitud de crédito dentro del sistema.
 * Contiene información general, estado, historial y documentos relacionados
 * que ayudan a determinar el puntaje de riesgo del solicitante.
 */
@Entity
@Table(name = "credit_applications",
       indexes = {
           @Index(name = "idx_credit_app_company_id", columnList = "company_id"),
           @Index(name = "idx_credit_app_status", columnList = "status")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreditApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_credit_application", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    @ToString.Exclude
    private Company company;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CreditStatus status;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths; // Plazo del crédito en meses

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_purpose", length = 50, nullable = false)
    private CreditPurpose creditPurpose;

    /**
     * Comentario de estado/observación actual del operador.
     * Es el comentario "visible" asociado al registro actual,
     * no el historial (el historial se guarda en CreditApplicationHistory).
     */
    @Column(name = "operator_comments", columnDefinition = "TEXT")
    private String operatorComments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //puntaje de riesgo
    @Column(name = "risk_score")
    private Integer riskScore;

    // 🔹 Nueva relación: documentos asociados a la solicitud
    @OneToMany(mappedBy = "creditApplication", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default
    private List<RiskDocument> riskDocuments = new ArrayList<>();
    /**
     * Historial de auditoría / acciones (bidireccional).
     * - Cascade ALL + orphanRemoval = true: si borras la solicitud, borras su historial.
     * - Fetch LAZY por defecto para evitar cargar historial en consultas normales.
     */
    @OneToMany(mappedBy = "creditApplication", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default
    private List<CreditApplicationHistory> history = new ArrayList<>();

    // ---------- Helper methods to maintain bidirectional relationship ----------
    public void addHistory(CreditApplicationHistory entry) {
        if (entry == null) return;
        entry.setCreditApplication(this);
        this.history.add(entry);
    }

    public void removeHistory(CreditApplicationHistory entry) {
        if (entry == null) return;
        entry.setCreditApplication(null);
        this.history.remove(entry);
    }

    public void addRiskDocument(RiskDocument doc) {
        if (doc == null) return;
        doc.setCreditApplication(this);
        this.riskDocuments.add(doc);
    }

    public void removeRiskDocument(RiskDocument doc) {
        if (doc == null) return;
        doc.setCreditApplication(null);
        this.riskDocuments.remove(doc);
    }
    public void calculateRiskScore() {
        if (riskDocuments == null || riskDocuments.isEmpty()) {
            this.riskScore = 0;
            return;
        }
        this.riskScore = riskDocuments.stream()
                .mapToInt(doc -> doc.getScoreImpact() != null ? doc.getScoreImpact() : 0)
                .sum();
    }

}

