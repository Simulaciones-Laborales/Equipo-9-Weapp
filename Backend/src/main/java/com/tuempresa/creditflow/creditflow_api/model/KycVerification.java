package com.tuempresa.creditflow.creditflow_api.model;

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

    // URLs de los documentos subidos a Cloudinary
    @Column(length = 1024)
    private String selfieUrl;

    @Column(length = 1024)
    private String dniFrontUrl;

    @Column(length = 1025)
    private String dniBackUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void onCreate() {
        submissionDate = LocalDateTime.now();
        if (status == null) status = KycStatus.PENDING;
    }
}