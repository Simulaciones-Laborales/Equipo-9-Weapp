package com.tuempresa.creditflow.creditflow_api.model;

import com.tuempresa.creditflow.creditflow_api.enums.KycEntityType;
import com.tuempresa.creditflow.creditflow_api.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kyc_verifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class KycVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idKyc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus status;

    @Column(columnDefinition = "TEXT")
    private String verificationNotes;
    @Column(columnDefinition = "TEXT")
    private String externalReferenceId; // ID del proveedor (Sumsub)
    private LocalDateTime submissionDate;
    private LocalDateTime verificationDate;

    // Tipo de verificación
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycEntityType entityType; // USER o COMPANY

    // Documentos genéricos (urls a Cloudinary)
    @Column(length = 1024)
    private String document1Url;  // Ej: DNI frente o Constancia CUIT

    @Column(length = 1024)
    private String document2Url;  // Ej: DNI dorso o Estatuto social

    @Column(length = 1024)
    private String document3Url;  // Ej: Selfie o Comprobante de domicilio fiscal
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @PrePersist
    public void onCreate() {
        submissionDate = LocalDateTime.now();
        if (status == null) status = KycStatus.PENDING;
    }
}