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

    private String verificationNotes;
    private String externalReferenceId; // ID del proveedor (Sumsub)
    private LocalDateTime submissionDate;
    private LocalDateTime verificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void onCreate() {
        submissionDate = LocalDateTime.now();
        status = KycStatus.PENDING;
    }
}