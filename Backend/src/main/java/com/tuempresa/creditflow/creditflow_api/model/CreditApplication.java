package com.tuempresa.creditflow.creditflow_api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
}

